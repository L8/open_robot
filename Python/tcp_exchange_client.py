# TCP client                                                                                                                                                                                      
import socket
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
ip_addr = raw_input("Enter IP: -->  ")
client_socket.connect((ip_addr, 8080))
while (1):
    msg = raw_input ("Message Please: -->  ")
    if (msg == "q"):
        client_socket.close()
        break;
    client_socket.send(msg + "\n")
    print client_socket.recv(512)







