using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Linq;
using System.ServiceProcess;
using System.Text;
using System.Net.NetworkInformation;

namespace SessionCheckService
{
    /// <summary>
    ///   サービスとして動作する、周期的にログオンユーザの情報を確認するプロセス
    /// </summary>
    public partial class RDPSessionCheckService : ServiceBase
    {
        private System.Timers.Timer myTimer = null;
        private int watchingTcpPort = 3389;
        private string outputSerialPort = null;
        private string previousLcdMessage = "";

        /// <summary>
        ///   コンストラクタ
        /// </summary>
        public RDPSessionCheckService()
        {
            InitializeComponent();
            if (!System.Diagnostics.EventLog.SourceExists("RDPSessionCheck"))
            {
                System.Diagnostics.EventLog.CreateEventSource("RDPSessionCheck", "RDPSessionLog");
            }
            eventLogger.Source = "RDPSessionCheck";
            eventLogger.Log = "RDPSessionLog";
        }

        /// <summary>
        ///   開始
        /// </summary>
        /// <param name="args"></param>
        protected override void OnStart(string[] args)
        {
            // デフォルトパラメータ
            int waitSec = 15;            // 15sec : 最小1秒
            outputSerialPort = "COM4";  // シリアルポート
            watchingTcpPort = 3389;      // Tcpポート

            // アーギュメントの解析
            //  アーギュメントで指定可能なパラメータ
            //    /LCD:COMxx データを出力するCOMポートの番号（デフォルト：COM4）
            //    /TCP:nnnnn 接続を監視するTCPポートの番号（デフォルト：3389）
            //    /WATCH:mmm 監視を確認する時間の間隔（単位：秒、デフォルト:15）
            foreach (string str in args)
            {
                int index = str.IndexOf("/LCD:");
                if (index >= 0)
                {
                    // '/LCD:COMss' を抽出する （送信するCOMポート番号）
                    outputSerialPort = str.Substring(index + 5);
                }
                index = str.IndexOf("/WATCH:");
                if (index >= 0)
                {
                    // '/WATCH:nnn' を抽出する（監視周期、単位は秒）
                    string watchTime = str.Substring(index + 7);
                    waitSec = System.Int32.Parse(watchTime);
                }
                index = str.IndexOf("/TCP:");
                if (index >= 0)
                {
                    // '/TCP:pppp' を抽出する（チェックするTCPポート番号）
                    string tcpPort = str.Substring(index + 5);
                    watchingTcpPort = System.Int32.Parse(tcpPort);
                }
            }

            // タイマーの間隔を設定して周期的に確認する設定
            if (waitSec < 0)
            {
                waitSec = 1;
            }
            NewTimer(waitSec * 1000);
            StartTimer();

            string loggingString = "START WATCHING TCP Port : " + watchingTcpPort;
            loggingString = loggingString + ", duration = " + waitSec + "sec";

            // 出力用LCDの設定（シリアルポート）
            if (outputSerialPort.Length > 3)
            {
                loggingString = loggingString + ", LCD : " + outputSerialPort;
                resetComPort(outputSerialPort);
            }            
            eventLogger.WriteEntry(loggingString);

            // 起動時には、現在の接続先PCを確認する
            checkConnectedPc();
        }


        /// <summary>
        ///  停止
        /// </summary>
        protected override void OnStop()
        {
            this.RequestAdditionalTime(2000);
            eventLogger.WriteEntry("OnStop");
        }

        /// <summary>
        ///   一時停止
        /// </summary>
        protected override void OnPause()
        {
            eventLogger.WriteEntry("OnPause");
        }

        /// <summary>
        ///   再開
        /// </summary>
        protected override void OnContinue()
        {
            eventLogger.WriteEntry("OnContinue");
        }

        /// <summary>
        ///   終了
        /// </summary>
        protected override void OnShutdown()
        {
            eventLogger.WriteEntry("OnShutdown");
            DisposeTimer();
        }

        /// <summary>
        ///   タイムアウトの受信（周期的実行）
        /// </summary>
        /// <param name="source"></param>
        /// <param name="e"></param>
        public void OnTimerEvent(object source, System.Timers.ElapsedEventArgs e)
        {
            // 現在の接続先PCを確認する
            checkConnectedPc();
        }

        /// <summary>
        ///   IPアドレスからホスト名を取得する
        /// </summary>
        /// <param name="ip">IPアドレス</param>
        /// <returns>ホスト名</returns>
        private string getHostName(System.Net.IPAddress ip)
        {
            try
            {
                System.Net.IPHostEntry hostInfo = System.Net.Dns.GetHostEntry(ip);
                return (hostInfo.HostName);
            }
            catch (Exception ex)
            {
                //ex.ToString();
            }
            return ("???");
        }

        /// <summary>
        ///   接続先PCの情報を取得する
        /// </summary>
        /// <returns>ログインユーザの情報</returns>
        private void checkConnectedPc()
        {
            string message = "!\\";
            String result = "";
            try
            {
                IPGlobalProperties properties = IPGlobalProperties.GetIPGlobalProperties();
                TcpConnectionInformation[] connections = properties.GetActiveTcpConnections();

                foreach (TcpConnectionInformation t in connections)
                {
                    if (t.LocalEndPoint.Port == watchingTcpPort)
                    {
                        // 監視対象のポートに接続している人が見つかった！
                        result = result +  getHostName(t.RemoteEndPoint.Address) + "[" + t.RemoteEndPoint.Address + ":" + t.RemoteEndPoint.Port + "]";
                        result = result + "  " + t.State + "\r\n";

                        // LCD表示用メッセージを作成する
                        message = getHostName(t.RemoteEndPoint.Address) + "!" + t.RemoteEndPoint.Address.ToString() + "\\";
                    }
                }
            }
            catch (Exception ex)
            {
                result = ex.ToString();
            }

            // シリアルポート接続中の時にはメッセージを送出する
            if (outputSerialPort.Length > 3)
            {
                sendMessageToPort(message);
            }

            // 前回表示したメッセージと変わっていた時にはログを出力する
            if (message != previousLcdMessage)
            {
                if (result.Length < 1)
                {
                    result = "(none)";
                }
                previousLcdMessage = message;
                result = "CHANGED PC: " + result;
                eventLogger.WriteEntry(result);
            }
            return;
        }

        /// <summary>
        ///   タイマーの生成
        /// </summary>
        private void NewTimer(int millisec)
        {
            myTimer = new System.Timers.Timer();
            myTimer.Enabled = true;
            myTimer.AutoReset = true;
            myTimer.Interval = millisec;
            myTimer.Elapsed += new System.Timers.ElapsedEventHandler(OnTimerEvent);
        }

        /// <summary>
        ///   タイマーの開始
        /// </summary>
        private void StartTimer()
        {
            myTimer.Start();
        }

        /// <summary>
        ///   タイマーの停止
        /// </summary>
        private void StopTimer()
        {
            myTimer.Stop();
        }

        /// <summary>
        ///   タイマーの削除
        /// </summary>
        private void DisposeTimer()
        {
            myTimer.Dispose();
        }

        /// <summary>
        ///   シリアルポートをオープンする
        /// </summary>
        private void resetComPort(string portName)
        {
            try
            {
                // COMポートの再オープン、データの初期化
                serialPort1.Close();
                serialPort1.PortName = portName;
                serialPort1.BaudRate = 9600;
                serialPort1.DataBits = 8;
                serialPort1.StopBits = System.IO.Ports.StopBits.One;
                serialPort1.Parity = System.IO.Ports.Parity.None;

                serialPort1.Open();
            }
            catch (Exception)
            {
                //
            }
        }

        /// <summary>
        ///   シリアルポートにデータを送信する
        /// </summary>
        private void sendMessageToPort(string message)
        {
            try
            {
                serialPort1.Write(message);
            }
            catch (Exception)
            {
                //
            }
        }

        /// <summary>
        ///   データを受信した時の処理
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void serialPort1_DataReceived(object sender, System.IO.Ports.SerialDataReceivedEventArgs e)
        {
            // データを受信した！ 
            System.IO.Ports.SerialPort port = (System.IO.Ports.SerialPort)sender;
            char[] rxArea = new char[8192];
            int readByte = serialPort1.Read(rxArea, 0, 100);
            string receivedMessage = new string(rxArea);

            try
            {
                // ログメッセージを作成する
                string logMessage = "RX: " + receivedMessage;

                // 受信データをログに出力する
                eventLogger.WriteEntry(logMessage);
            }
            catch (Exception)
            {
                //
            }
        }

    }
}
