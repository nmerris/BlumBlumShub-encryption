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

        // the initialValue entered by user, need BigInteger again because could overflow Long if user
        // types in too large a hex number, for example FFFFFFFF will overflow Long after it is squared
        BigInteger initialValue = BigInteger.ZERO;

        // get user input
        System.out.println("Enter data: ");
        String data = scanner.nextLine();

        System.out.println("Enter data length: ");
        int dataLength = scanner.nextInt();
        scanner.nextLine(); // consume the dangling \n


        // get the initial hex value, do some basic validation
        boolean invalidInput;
        do {
            invalidInput = false;
            System.out.println("Enter initial value in hex: ");
            String initialValueString = scanner.nextLine();
            try {
                // convert the entered String to a BigInteger, input will always be in hexadecimal
                initialValue = new BigInteger(initialValueString, 16);
            } catch (NumberFormatException e) {
                // user did not enter a valid hex value
                System.out.println("Please enter a valid hexidecimal value...");
                invalidInput = true;
            }
        } while (invalidInput);


        // NOTE: in Java, all chars are stored as 2 bytes (4 hex digits)
        // this challenge assumes 1 byte per character, so I'm chopping off the unused first byte
        // this happens automatically when converting data to an array of Bytes
        byte[] inputByteArray = data.getBytes();


        // holds the encrypted output String
        StringBuilder output = new StringBuilder(dataLength);

        // calculate the initial value: value = (value * value) mod M
        BigInteger value = initialValue.pow(2).mod(M);


//        value = value.mod(M);
//        BigInteger value = BigInteger.valueOf(initialValue * initialValue).mod(M);

//        System.out.println(String.format("length of value & lowestByteMask: %d", value.and(lowestByteMask).toByteArray().length));

//        byte lowByte = value.toByteArray()[1];

//        System.out.println(String.format("initial value squared: %20X", initialValue.pow(2)));
//        System.out.println(String.format("initial value after squaring and mod: %10X", value));
//        System.out.println(String.format("inputByteArray[0]: %10X", inputByteArray[0]));
//        System.out.println("initial value.toByteArray().length: " + value.toByteArray().length);
//        System.out.println("initial value.and(lowestByteMask).toByteArray().length: " + value.and(lowestByteMask).toByteArray().length);

        // get the lowest byte, keep in mind value may have varying numbers of bytes
        byte lowByte = value.and(lowestByteMask).toByteArray()[value.and(lowestByteMask).toByteArray().length - 1];

        // XOR lowByte with the first byte in inputByteArray, cast to a byte to chop off the unwanted preceeding digits
        output.append(String.format("%02X ", (byte) (lowByte ^ inputByteArray[0])));

        // loop through the rest of the input, one byte at a time
        for (int i = 1; i < dataLength; i++) {

            // square value, then get mod M
            value = value.pow(2).mod(M);

            // get the lowest byte, this is the current key
            lowByte = value.and(lowestByteMask).toByteArray()[value.and(lowestByteMask).toByteArray().length - 1];

            // append the encrypted byte to the output String
            output.append(String.format("%02X ", (byte) (lowByte ^ inputByteArray[i])));

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
