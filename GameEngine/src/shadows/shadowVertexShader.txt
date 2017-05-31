#version 150

in vec3 in_position;

uniform mat4 mvpMatrix;

void main(void){

	gl_Position = mvpMatrix * vec4(in_position, 1.0);

}