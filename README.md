# BlumBlumShub-encryption
my implementation of the Blum Blum Shub encryption algorithm

### SAMPLE ENCODING OF DATA:

Enter data:  
Hello world, how are you doing today?   
Enter data length:    
37   
Enter initial value in hex:   
fead4801   
Would you like to ENCODE or DECODE? (E/D)   
e   
OUTPUT ENCODED: ACC6C5E955FB26045BBF27BF223EA44A2D926DBBD679088400F629CD1C829BB5BF215C1FC8   

### SAMPLE DECODING OF SAME DATA:

Enter data:   
ACC6C5E955FB26045BBF27BF223EA44A2D926DBBD679088400F629CD1C829BB5BF215C1FC8   
Enter data length:    
37   
Enter initial value in hex:    
fead4801   
Would you like to ENCODE or DECODE? (E/D)   
d   
OUTPUT DECODED: Hello world, how are you doing today?   

### SAMPLE USING SAME DATA BUT WITH WRONG DECODE INITIAL VALUE
Enter data:    
ACC6C5E955FB26045BBF27BF223EA44A2D926DBBD679088400F629CD1C829BB5BF215C1FC8   
Enter data length:    
37   
Enter initial value in hex:    
1234567   
Would you like to ENCODE or DECODE? (E/D)   
D   
�`+*�l���|̂A�I�q�   
