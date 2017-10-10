package com.nmerris;

import java.math.BigInteger;
import java.util.Scanner;

// author: Nathan Merris
public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // squaring the initialValue for BBS PRNG can result in very large numbers
        // since we don't know how large initialValue may be, best to use BigInteger here
        // or we may overflow and get bogus results
        final BigInteger M = BigInteger.valueOf(0xE2089EA5L);

        // used to select the lowest byte of a number
        final BigInteger lowestByteMask = BigInteger.valueOf(0xFFL);

        // the initialValue entered by user, need BigInteger again because could overflow long if user
        // types in too large a hex number, for example FFFFFFFF will overflow long after it is squared
        BigInteger initialValue = BigInteger.ZERO;

        // get user input
        System.out.println("Enter data: ");
        String data = scanner.nextLine();





        // get the initial hex value, do some basic validation
//        long initialValue = 0;
        boolean invalidInput;
        do {
            invalidInput = false;
            System.out.println("Enter initial value in hex: ");
            String initialValueString = scanner.nextLine();
            try {
                // convert the entered String to a BigInteger, input will be in hexadecimal
                initialValue = new BigInteger(initialValueString, 16);
//                initialValue = BigInteger.valueOf(initialValueString, 16);
            } catch (NumberFormatException e) {
                // user did not enter a valid hex value
                System.out.println("Please enter a valid hexidecimal value...");
                invalidInput = true;
            }
        } while (invalidInput);

        // the number of UTF-16 chars in the entered String, each is represented as 4 hex digits
        // so this is twice the number of bytes in data, as each char is two bytes
        int dataLength = data.length();


        // NOTE: in Java, all chars are stored as 2 bytes (4 hex digits)
        // a String is an array of
        // this challenge assumes 1 byte per character, so I'm chopping off the unused first byte
        // this happens automatically when converting data to an array of Bytes
        byte[] inputByteArray = data.getBytes();
        for (byte b : inputByteArray) {
            System.out.println(String.format("inputByteArray[...] = %10X", b));
        }


        // holds the encrypted output String
        // which will have exactly the same number of bytes as the input String had chars
        // because we are only using 1 byte (two hex digits) per output char, as specified
        StringBuilder output = new StringBuilder(dataLength);

        // calculate the initial value: value = (value * value) mod M
        BigInteger value = initialValue.pow(2);
        value = value.mod(M);
//        BigInteger value = BigInteger.valueOf(initialValue * initialValue).mod(M);

//        System.out.println(String.format("length of value & lowestByteMask: %d", value.and(lowestByteMask).toByteArray().length));

//        byte lowByte = value.toByteArray()[1];

//        System.out.println(String.format("initial value squared: %20X", initialValue.pow(2)));
//        System.out.println(String.format("initial value after squaring and mod: %10X", value));
//        System.out.println(String.format("inputByteArray[0]: %10X", inputByteArray[0]));
//        System.out.println("initial value.toByteArray().length: " + value.toByteArray().length);
//        System.out.println("initial value.and(lowestByteMask).toByteArray().length: " + value.and(lowestByteMask).toByteArray().length);

        byte lowByte;
        lowByte = value.and(lowestByteMask).toByteArray()[value.and(lowestByteMask).toByteArray().length - 1];
//        byte lowByte = (byte) (value & 0xFF);
//        System.out.println(String.format("    first key is (lowest byte of value): %4x", lowByte));

        // XOR the key with the first char of the input String, then append to output, cast to a byte to ignore all but the lowest byte
        // uppercase X just makes the output hex letter all uppercase
        output.append(String.format("%02X ", (byte) (lowByte ^ inputByteArray[0])));

//        System.out.println(String.format("    and the first encoded output byte is: %4x", (lowByte ^ inputByteArray[0]) & 0xFF));


        for (int i = 1; i < dataLength; i++) {
//            System.out.println(String.format("for loop i: %d, starting value: %08X", i, value));
//            System.out.println(String.format("value squared: %020X", value.pow(2)));

            // square value, then get mod M
            value = value.pow(2);
            value = value.mod(M);

//            System.out.println("initial value.toByteArray().length: " + value.toByteArray().length);
//            System.out.println("initial value.and(lowestByteMask).toByteArray().length: " + value.and(lowestByteMask).toByteArray().length);

//            value = (value * value) % M;
//            System.out.println(String.format("value = value * value mod M, in hex: %08x", value));

            // get the lowest byte
//            System.out.println(String.format("length of value & lowestByteMask: %d", value.and(lowestByteMask).toByteArray().length));
            lowByte = value.and(lowestByteMask).toByteArray()[value.and(lowestByteMask).toByteArray().length - 1];
//            System.out.println(String.format("    key is (lowest byte of value): %04x", lowByte));

            byte encryptedByte = (byte) (lowByte ^ inputByteArray[i]);
//            byte encryptedByte = (byte) ((lowByte ^ inputByteArray[i]) & 0xFF);

            // append the encrypted byte to the output String
            output.append(String.format("%02X ", encryptedByte));
//            System.out.println(String.format("    output is: %04x", encryptedByte));

        }



        System.out.println("FINAL OUTPUT ENCRYPTED: " + output);


    }





    // CREDIT for this method: https://stackoverflow.com/questions/923863/converting-a-string-to-hexadecimal-in-java, Bogdan Calmac
    static String stringToHex(String string) {
        StringBuilder buf = new StringBuilder(200);
        for (char ch: string.toCharArray()) {
            if (buf.length() > 0)
                buf.append(' ');
            buf.append(String.format("%04x", (int) ch));
        }
        return buf.toString();
    }


}
