package com.nmerris;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Scanner;

// author: Nathan Merris
public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // get user input
        System.out.println("Enter data: ");
        String dataString = scanner.nextLine();

        System.out.println("Enter data length: ");
        int dataLength = scanner.nextInt();
        scanner.nextLine(); // consume the dangling \n

        // get the initial value as a hex
        System.out.println("Enter a positive initial value <= 99 999 999 in hex: ");
        String initialValueString = scanner.nextLine();

        try {
            // convert initialValueString to an int, user can enter an unsigned int here
            // so there are 2^32 = 0x99999999 possible initialValues, and must be a positive number
            int initialValue = Integer.parseUnsignedInt(initialValueString, 16);
            blumBlumShubify(dataString , dataLength, initialValue);
        } catch (NumberFormatException e) {
            System.out.println("Invalid initial value");
        }

    } // main


    private static void blumBlumShubify(String dataString, int dataLength, int initialValue) {

        // squaring the initialValue for BBS PRNG can result in very large numbers
        // since we don't know how large initialValue may be, best to use BigInteger here
        // or we may overflow and get bogus results
        final BigInteger M = BigInteger.valueOf(0xE2089EA5L);

        // used to select the lowest byte of a number
        final BigInteger lowestByteMask = BigInteger.valueOf(0xFFL);

        // if the number of chars entered as data is same as data length entered by user, must be encoding
        // otherwise, for the purposes of this simple program, we'll assume user wants to decode
        boolean encode = dataLength == dataString.length();

        // holds each byte of the input: could be either data to encode or decode
        byte[] data = new byte[dataLength];

        if (encode) {
            // this challenge assumes 1 byte per character, so I'm chopping off the unused first byte
            // this happens automatically when converting data to an array of Bytes
            // by default, the bytes are UTF-8 encoded
            data = dataString.getBytes();
        } else {
            // for decoding, we start with 2 hex digits per char, so need to combine consecutive groups
            // to get a starting array of bytes
            for (int i = 0; i < dataLength * 2; i += 2) {
                // get 2 consecutive bytes each time, so: 0,1 then 2,3 then 4,5 etc
                // substrings upper index is not inclusive, thus i + 2
                int tempByte = Integer.valueOf(dataString.substring(i, i + 2), 16);
                // put the newly created 'combo' byte in the input byte array
                data[i / 2] = (byte) (tempByte);
            }
        }
        
        
        
        
        

        // holds the encoded output String
        StringBuilder encodedOutputString = new StringBuilder(dataLength);

        // holds the decoded output bytes, these will be converted to a String when displaying to console
        byte[] decodedOutputByteArray = new byte[dataLength];

        // calculate the initial value: value = (value * value) mod M
        BigInteger value = BigInteger.valueOf(initialValue).pow(2).mod(M);

        // get the lowest byte: mask out everything except the lowest byte, convert to byte array,
        // then grab the lowest byte of the array
        // NOTE: value may have varying number of bytes, so need to get it's length and subtract 1 to get the lowest byte
        byte lowByte = value.and(lowestByteMask).toByteArray()[value.and(lowestByteMask).toByteArray().length - 1];

        // XOR lowByte with the first byte in data, cast to a byte to chop off the unwanted preceding digits
        if (encode) {
            encodedOutputString.append(String.format("%02X", (byte) (lowByte ^ data[0])));
        } else {
            decodedOutputByteArray[0] = (byte) (lowByte ^ data[0]);
        }

        // loop through the rest of the input, one byte at a time
        for (int i = 1; i < dataLength; i++) {

            // square value, then get mod M
            value = value.pow(2).mod(M);

            // get the lowest byte, this is the current key
            lowByte = value.and(lowestByteMask).toByteArray()[value.and(lowestByteMask).toByteArray().length - 1];

            if (encode) {
                // append the encoded byte to the output String, drop all preceding bytes by casting to byte
                encodedOutputString.append(String.format("%02X", (byte) (lowByte ^ data[i])));
            } else {
                // add this byte to the decoded array of bytes that will represent our decoded output
                decodedOutputByteArray[i] = (byte) (lowByte ^ data[i]);
            }
        }

        // display the output depending on if we are encoding or decoding
        if (encode) {
            System.out.println("OUTPUT ENCODED: " + encodedOutputString);
        } else {
            try {
                // display the decoded output, create a new String from the array of bytes using UTF8 encoding
                System.out.println("OUTPUT DECODED: " + new String(decodedOutputByteArray, "UTF8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }




    } // blumBlumShubify






}
