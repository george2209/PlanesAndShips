/*
 * Copyright (c) 2022.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.gdi.cavans.blender;

import static ro.sg.avioane.util.OpenGLProgramFactory.SHADER_VERTICES_WITH_NORMALS;
import static ro.sg.avioane.util.OpenGLProgramFactory.SHADER_VERTICES_WITH_OWN_COLOR;
import static ro.sg.avioane.util.OpenGLProgramFactory.SHADER_VERTICES_WITH_UV_DATA_MATERIAL;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZCoordinate;
import ro.sg.avioane.geometry.XYZTextureUV;
import ro.sg.avioane.geometry.XYZVertex;

public abstract class AbstractObjParser {

    public static final short PARSER_ENGINE_START = 0;
    public static final short PARSER_ENGINE_MATERIAL_NAME = 1;
    public static final short PARSER_ENGINE_MATERIAL_AMBIENT_COLOR = 2;
    public static final short PARSER_ENGINE_MATERIAL_DIFFUSE_COLOR = 3;
    public static final short PARSER_ENGINE_MATERIAL_KA_TEXTURE = 4;
    public static final short PARSER_ENGINE_MATERIAL_KD_TEXTURE = 5;
    public static final short PARSER_ENGINE_MATERIAL_FINISHED = 6;
    public static final short PARSER_ENGINE_COMPONENT_NAME = 7;
    public static final short PARSER_ENGINE_COMPONENT_MESHES_NO = 8;
    public static final short PARSER_ENGINE_MESH_MATERIAL_ID = 9;
    public static final short PARSER_ENGINE_MESH_VERTEX_NO = 10;
    public static final short PARSER_ENGINE_MESH_VERTEX_PROPERTIES = 11;
    public static final short PARSER_ENGINE_VERTEX_POSITION = 12;
    public static final short PARSER_ENGINE_VERTEX_INDEX_SIZE = 13;
    public static final short PARSER_ENGINE_VERTEX_INDEX = 14;
    public static final short PARSER_ENGINE_FINISHED = 15;

    protected short iSTATUS_ENGINE = PARSER_ENGINE_START;
    protected final Context iContext;

    public AbstractObjParser(final Context context){
        this.iContext = context;
    }

    protected boolean getByteArrayAsBoolean(final BufferedInputStream inputStream)throws IOException {
        byte[] data = new byte[1];
        inputStream.read(data, 0, 1);
        return data[0] == 1;
    }

    protected short getByteArrayAsShort(final BufferedInputStream inputStream)throws IOException {
        byte[] data = new byte[Short.BYTES];
        inputStream.read(data, 0, Short.BYTES);
        final ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getShort();
    }

    protected int getByteArrayAsInt(final BufferedInputStream inputStream) throws IOException {
        byte[] data = new byte[Integer.BYTES];
        inputStream.read(data, 0, Integer.BYTES);
        final ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getInt();
    }

    protected float getByteArrayAsFloat(final BufferedInputStream inputStream)throws IOException {
        final byte[] data = new byte[Float.BYTES];
        inputStream.read(data, 0, Float.BYTES);
        final ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getFloat();
    }

    protected String getByteArrayAsString(
            final BufferedInputStream inputStream,
            final Charset charSet,
            final int charLength)throws IOException {
        final byte[] data = new byte[charLength];
        inputStream.read(data, 0, charLength);
        return new String(data, charSet);
    }

    protected XYZVertex getByteArrayAsVertex(
            final ByteBuffer bb, int index, final int vertexStride,
            final int verticesProperties) throws IOException{
        //final XYZVertex
        XYZTextureUV textureUV = null;
        XYZCoordinate vertexNormal = null;
        XYZColor vertexColor = null;

        //x,y,z
        final XYZCoordinate vertexPosition = new XYZCoordinate(
                bb.getFloat(index),
                bb.getFloat(index + Float.BYTES),
                bb.getFloat(index + Float.BYTES * 2));
        index+= Float.BYTES * 3;

        if((verticesProperties & SHADER_VERTICES_WITH_UV_DATA_MATERIAL) > 0){
            textureUV = new XYZTextureUV(bb.getFloat(index),
                    bb.getFloat(index + Float.BYTES));
            index+= Float.BYTES * 2;
        }
        if((verticesProperties & SHADER_VERTICES_WITH_NORMALS) > 0){
            vertexNormal = new XYZCoordinate(
                    bb.getFloat(index),
                    bb.getFloat(index + Float.BYTES),
                    bb.getFloat(index + (2 * Float.BYTES)));
            index+= Float.BYTES * 3;
        }
        if((verticesProperties & SHADER_VERTICES_WITH_OWN_COLOR) > 0){
            vertexColor = new XYZColor(bb.getFloat(index),
                    bb.getFloat(index + Float.BYTES),
                    bb.getFloat(index + (2 * Float.BYTES)),
                    bb.getFloat(index + (3 * Float.BYTES)));
        }
        return new XYZVertex(vertexPosition, vertexNormal, textureUV, vertexColor);
    }

    protected XYZColor getByteArrayAs4DColor(
            final BufferedInputStream inputStream) throws IOException{
        return new XYZColor(
                this.getByteArrayAsFloat(inputStream),
                this.getByteArrayAsFloat(inputStream),
                this.getByteArrayAsFloat(inputStream),
                this.getByteArrayAsFloat(inputStream));
    }

    protected XYZTextureUV getByteArrayAsTextureUV(
            final BufferedInputStream inputStream) throws IOException{
        final XYZTextureUV xyzCoordinate = new XYZTextureUV(
                this.getByteArrayAsFloat(inputStream),
                this.getByteArrayAsFloat(inputStream));
        return xyzCoordinate;
    }

    //public abstract boolean processStream();
}
