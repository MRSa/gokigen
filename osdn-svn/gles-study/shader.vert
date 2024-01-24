uniform mat4 u_modelViewProjMatrix;
uniform mat4 u_normalMatrix;
uniform vec3 lightDir;
 
attribute vec3 vNormal;
attribute vec4 vColor;
attribute vec4 vPosition;
 
varying float  g_Dot;
varying vec4   g_vVSColor;
 
void main()
{
    gl_Position = u_modelViewProjMatrix * vPosition;
    g_vVSColor = vColor;
    vec4 transNormal = u_normalMatrix * vec4(vNormal, 1);
    g_Dot = max(dot(transNormal.xyz, lightDir), 0.0);
}
