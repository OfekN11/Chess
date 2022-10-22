package srv.api;

import Business.Place;
import srv.api.Messages.*;

import java.util.LinkedList;
import java.util.List;

public class MessageEncoderDecoder implements MessageEncoderDecoderInterface<Message> {
    byte[] opcode = new byte[2];
    List<Byte> bytes = new LinkedList<>();
    int lenOpcode = 0;


    @Override
    public Message decodeNextByte(byte nextByte) {
        if (nextByte == ';') {
            short op = bytesToShort(opcode);
            List<Byte> tmp = bytes;
            bytes = new LinkedList<>();
            lenOpcode = 0;
            switch (op) {
                case 1:
                    return new PlaceMessage(tmp);
                case 2:
                    return new StringMessage(tmp);
                case 3:
                    return new PlacesMessage(tmp);
                case 4:
                    return new BoardContentMessage(tmp);
            }
        } else {
            if (lenOpcode < 2) {
                opcode[lenOpcode] = nextByte;
                lenOpcode++;
            } else
                bytes.add(nextByte);
        }
        return null;
    }

    @Override
    public byte[] encode(Message message) {
        short opcode = message.getOpcode();
        byte[] msg = new byte[0];
        byte[] content;

        switch (opcode) {
            case 1:   //PlaceMessage
                PlaceMessage placeMessage = (PlaceMessage) message;
                content = new byte[3];
                content[0] = intToByte(placeMessage.getPlace().getRow());
                content[1] = ' ';
                content[2] = intToByte(placeMessage.getPlace().getColumn());
                break;

            case 2: // string message
                StringMessage stringMessage = (StringMessage) message;
                content = stringMessage.getMsg().getBytes();
                break;

            case 3:
                PlacesMessage placesMessage = (PlacesMessage) message;
                StringBuilder builder = new StringBuilder();
                for (Place p :
                        placesMessage.getCollection()) {
                    builder.append(p.getRow());
                    builder.append(' ');
                    builder.append(p.getColumn());
                    builder.append(' ');
                }
                String output = builder.toString();
                output = output.substring(0,output.length()-1);
                content = output.getBytes();
                break;

            case 4: // BoardMessage
                BoardContentMessage boardMessage = (BoardContentMessage) message;
                content = boardMessage.getBoardContent().getBytes();
                break;
            default:
                content = new byte[0];

        }

        byte[] delimiter = new byte[1];
        delimiter[0] = (byte) ';';
        return concatBytesArrays(shortToBytes(opcode), content,delimiter);
    }

    private byte intToByte(int i) {
        char c = (char) i;
        c += '0';
        return (byte) c;
    }


    public static short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }

    static byte[] concatBytesArrays(byte[]... bytes) {
        if (bytes.length < 2)
            throw new IllegalArgumentException();

        byte[] output = bytes[0];
        for (int i = 1; i < bytes.length; i++)
            output = mergeArr(output, bytes[i]);

        return output;
    }

    static byte[] mergeArr(byte[] arr1, byte[] arr2) {
        byte[] output = new byte[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, output, 0, arr1.length);
        System.arraycopy(arr2, 0, output, arr1.length, arr2.length);
        return output;
    }

    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
}
