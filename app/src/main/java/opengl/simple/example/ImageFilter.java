package opengl.simple.example;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


/**
 * Shader Variable Types: –
 * <p>
 * Attribute: – This is for vertex shaders. It is used to apply values to individual vertices.
 * <p>
 * Uniforms: – Applied to both Vertex and Fragment shader. Its value does not change during the frame.
 * <p>
 * Varying: – This is used to share data of the Vertex Shader with the Fragment Shader.
 */

public class ImageFilter {

    FloatBuffer verticesBuffer;
    FloatBuffer textureBuffer;

    int vertexShader = 0;
    int fragmentShader = 0;
    int program = 0;


    private static final String vertexShaderCode = "attribute vec4 aPosition;" +
            "attribute vec2 aTexPosition;" +
            "varying vec2 vTexPosition;" +
            "void main() {" +
            "  gl_Position = aPosition;" +
            "  vTexPosition = aTexPosition;" +
            "}";

    private static final String fragmentShaderCode1 = "precision mediump float;" +
            "uniform sampler2D uTexture;" +
            "varying vec2 vTexPosition;" +
            "void main() {" +
            "  gl_FragColor = texture2D(uTexture, vTexPosition);" +
            "}";


    //Deform
    private static final String fragmentShaderCode2 = "precision mediump float;" +
            "uniform sampler2D uTexture;" +
            "varying vec2 vTexPosition;" +
            "void main() {" +
            "  vec2 cen = vec2(0.5,0.5) - vTexPosition.xy;" +
            "  vec2 mcen = -0.07* log(length(cen)) * normalize(cen);" +
            "  gl_FragColor = texture2D(uTexture, vTexPosition.xy-mcen);" +
            "}";


    // Color/Brightness/Hue
    private static final String fragmentShaderCode3 = "precision mediump float;" +
            "uniform sampler2D uTexture;" +
            "  varying vec2 vTexPosition;" +
            " void main()" +
            " { " +
            "  const vec3 W = vec3(0.2125, 0.1754, 0.0721);" +
            "     vec3 irgb = texture2D(uTexture, vTexPosition).rgb;" +
            "     float luminance = dot(irgb, W);" +
            "     gl_FragColor = vec4(luminance, luminance, luminance, 1.);" +
            "  }";

    private String fragmentShaderCode = fragmentShaderCode1;


    private static final float[] positionVertices = {-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};

    private static final float[] textureVertices = {0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f};


    public ImageFilter() {
        initializeBuffers();
        initializeProgram();
    }

    public ImageFilter(int filter) {

      setFragmentShaderCode(filter);
        initializeBuffers();
        initializeProgram();

    }

    private void setFragmentShaderCode(int filter) {
        switch (filter) {
            case 0:
                fragmentShaderCode = fragmentShaderCode1;
                break;
            case 1:
                fragmentShaderCode = fragmentShaderCode2;
                break;
            case 2:
                fragmentShaderCode = fragmentShaderCode3;
                break;
        }
    }

    /**
     * 1. we need to put our float arrays into a float buffer
     */
    private void initializeBuffers() {
        ByteBuffer byteBuff = ByteBuffer.allocateDirect(positionVertices.length * 4);
        byteBuff.order(ByteOrder.nativeOrder());
        verticesBuffer = byteBuff.asFloatBuffer();
        verticesBuffer.put(positionVertices);
        verticesBuffer.position(0);

        byteBuff = ByteBuffer.allocateDirect(textureVertices.length * 4);
        byteBuff.order(ByteOrder.nativeOrder());
        textureBuffer = byteBuff.asFloatBuffer();
        textureBuffer.put(textureVertices);
        textureBuffer.position(0);


    }

    /**
     * 1. Initialize the program.
     * 2. Add the fragmentShaderCode to Opengl
     * 3. Add the vertexShaderCode to Opengl
     * 4. Attach both fragment shader and vertex shader to initialized program
     */
    private void initializeProgram() {
        vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader, vertexShaderCode);
        GLES20.glCompileShader(vertexShader);

        fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader, fragmentShaderCode);
        GLES20.glCompileShader(fragmentShader);

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);

        GLES20.glLinkProgram(program);
    }

    /**
     * Get reference to positions of variables in the shader code
     *
     * @param texture
     */
    public void draw(int texture) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glUseProgram(program); // Add program to OpenGL ES environment
        GLES20.glDisable(GLES20.GL_BLEND);

        int positionHandle = GLES20.glGetAttribLocation(program, "aPosition"); // get reference to vertex shader’s aPosition variable
        int textureHandle = GLES20.glGetUniformLocation(program, "uTexture"); // get handle to vertex shader’s uTexture variable
        int texturePositionHandle = GLES20.glGetAttribLocation(program, "aTexPosition"); // get handle to vertex shader’s aTextPosition variable

        GLES20.glVertexAttribPointer(texturePositionHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer); // add the texturebuffer (containing float array) to the reference gotten above
        GLES20.glEnableVertexAttribArray(texturePositionHandle); // Enable a handle to the texture positions

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);//
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture); //pass the texture to openGL
        GLES20.glUniform1i(textureHandle, 0);

        // Prepare the position data
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, verticesBuffer); //add the verticeBuffer (float buffer) to the reference gotten above
        GLES20.glEnableVertexAttribArray(positionHandle);// Enable a handle to the  positions

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //Draw the object
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }


}
