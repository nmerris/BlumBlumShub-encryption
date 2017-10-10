package com.nmerris;

import java.io.UnsupportedEncodingException;
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
        BigInteger initialValue;

        // true if user wants to encode, false if decode
        boolean encode;

        // get user input
        System.out.println("Enter data: ");
        String data = scanner.nextLine();

        System.out.println("Enter data length: ");
        int dataLength = scanner.nextInt();
        scanner.nextLine(); // consume the dangling \n


        // get the initial value as a hex, save it as a BigInteger
        System.out.println("Enter initial value in hex: ");
        String initialValueString = scanner.nextLine();
        initialValue = new BigInteger(initialValueString, 16);

        // holds each byte of the input: could be either data to encode or decode
        byte[] inputByteArray = new byte[dataLength];

        System.out.println("Would you like to ENCODE or DECODE? (E/D)");
        // set boolean depending on if user wants to encode or decode
        encode = scanner.nextLine().equalsIgnoreCase("E");

        if (encode) {
            // user selected ENCODE

            // this challenge assumes 1 byte per character, so I'm chopping off the unused first byte
            // this happens automatically when converting data to an array of Bytes
            // by default, the bytes are UTF-8 encoded
            inputByteArray = data.getBytes();

        } else {
            // user selected DECODE

            // for decoding, we start with 2 hex digits per char, so need to combine consecutive groups
            // to get a starting array of bytes
            for (int i = 0; i < dataLength * 2; i += 2) {
                // get 2 consecutive bytes each time, so: 0,1 then 2,3 then 4,5 etc
                // substrings upper index is not inclusive, thus i + 2
                int tempByte = Integer.valueOf(data.substring(i, i + 2), 16);
                // put the newly created 'combo' byte in the input byte array
                inputByteArray[i / 2] = (byte) (tempByte);
            }
        }


        // holds the encoded output String
        StringBuilder encodedOutputString = new StringBuilder(dataLength);

        // holds the decoded output bytes, these will be converted to a String when displaying to console
        byte[] decodedOutputByteArray = new byte[dataLength];

        // calculate the initial value: value = (value * value) mod M
        BigInteger value = initialValue.pow(2).mod(M);

        // get the lowest byte: mask out everything except the lowest byte, convert to byte array,
        // then grabe the lowest byte of the array
        // NOTE: value may have varying number of bytes, so need to get it's length and subtract 1 to get the lowest byte
        byte lowByte = value.and(lowestByteMask).toByteArray()[value.and(lowestByteMask).toByteArray().length - 1];

        // XOR lowByte with the first byte in inputByteArray, cast to a byte to chop off the unwanted preceding digits
        if (encode) {
            encodedOutputString.append(String.format("%02X", (byte) (lowByte ^ inputByteArray[0])));
        } else {
            decodedOutputByteArray[0] = (byte) (lowByte ^ inputByteArray[0]);
        }

        // loop through the rest of the input, one byte at a time
        for (int i = 1; i < dataLength; i++) {

            // square value, then get mod M
            value = value.pow(2).mod(M);

            // get the lowest byte, this is the current key
            lowByte = value.and(lowestByteMask).toByteArray()[value.and(lowestByteMask).toByteArray().length - 1];

            if (encode) {
                // append the encoded byte to the output String, drop all preceding bytes by casting to byte
                encodedOutputString.append(String.format("%02X", (byte) (lowByte ^ inputByteArray[i])));
            } else {
                // add this byte to the decoded array of bytes that will represent our decoded output
                decodedOutputByteArray[i] = (byte) (lowByte ^ inputByteArray[i]);
            }

        }


        // display the output depending on if user selected 'encode' or 'decode'
        if (encode) {
            // display the encoded output
            System.out.println("OUTPUT ENCODED: " + encodedOutputString);
        } else {
            try {
                // display the decoded output, create a new String from the array of bytes using UTF8 encoding
                System.out.println("OUTPUT DECODED: " + new String(decodedOutputByteArray, "UTF8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


    } // main


}
