package jp.sourceforge.gokigen.clock;

public interface INumberValueProvider 
{
    public final int BUSY_A = 10;  // ビジーA記号
    public final int BUSY_B = 11;  // ビジーB記号
    public final int MINUS  = 12;  // マイナス記号
    public final int COLON  = 13;  // コロン記号
    public final int DOT    = 14;  // ドット記号
    public final int SPACE  = 15;  // 空白

    /**
     *  データを更新する
     */
    public abstract void update();
    
    /** 表示する桁数を取得する
     * 
     * @return  表示が必要な桁数
     */
    public abstract int getNumberOfDigits();

    /** 表示する桁の数字を取得する 
     * 
     * @param index
     * @return
     */
    public abstract int getNumber(int index);

}
