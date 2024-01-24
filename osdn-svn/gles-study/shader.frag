#ifdef GL_FLAGMENT_PRECISION_HIGH
  precision highp float;
#else
  precision mediump float;
#endif

varying float g_Dot;
varying vec4  g_vVSColor;
 
void main()
{
    gl_FragColor = vec4(g_vVSColor.x * g_Dot, g_vVSColor.y * g_Dot, g_vVSColor.z * g_Dot, g_vVSColor.a);
}
