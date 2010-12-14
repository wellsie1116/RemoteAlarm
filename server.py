#!/usr/bin/python

import bluetooth as bt
import threading
import sys

server_uuid = '00e130e3-02f8-4652-9214-5fe6ef394c56'

addr = 'D4:E8:B2:54:2B:7A'
client_uuid = '1f96dba5-6384-44fd-a500-a5cf3147e1a6'

def getSocket(addr, uuid):
    service_matches = bt.find_service(address=addr, uuid=uuid)

    if len(service_matches) > 0:
        match = service_matches[0]
        print list(match.keys())
        port, name, host = match['port'], match['name'], match['host']
        print("Connecting to ", name, host, port)
    else:
        return None

    sock = bt.BluetoothSocket(bt.RFCOMM)
    sock.connect((host, port))
    return sock


class CmdReader():
    def __init__(self, sock):
        self.sock = sock
    def __iter__(self):
        buf = ''
        while True:
            while buf.indes('\n') < 0:
                read = self.sock.read(1024) 
                if read == '':
                    return
                buf += read
            index = buf.index('\n')
            cmd, buf = buf[:index], buf[index+1:]
            yield cmd


class AlarmServer(threading.Thread):
    def __init__(self):
        threading.Thread.__init__(self)
        self.running = True
        self.server_sock = bt.BluetoothSocket(bt.RFCOMM)
        self.server_sock.bind(('', bt.PORT_ANY))
        self.server_sock.listen(1)

        self.sock = None

        bt.advertise_service(self.server_sock, "RemoteAlarm", server_uuid)

    def run(self):
        print("Server listening")
        print dir(self.server_sock)
        while self.running:
            self.sock, address = self.server_sock.accept()
            if address != addr:
                print("Invalid client (%s) attempted to connect" % (address))
                self.sock.close()
                self.sock = None
                continue
            for cmd in CmdReader(self.sock):
                print "Retrieved cmd from user: %s" % cmd
            self.sock.close()
        self.stop()

    def stop(self):
        if self.running:
            print("Server shutting down")
            self.running = False
            bt.stop_advertising(self.server_sock)
            self.server_sock.close()
            print "Server shut down"


def parse_cmd(cmd):
    index = cmd.find(' ')
    if index < 0:
        op, args = cmd, ''
    else:
        op, args = cmd[:index], cmd[index+1:]
    return op, args


class ClientSocket:
    def __init__(self):
        self.sock = None
        self.timer = None
    def disconnect(self):
        print "killing socket"
        self.sock.close()
        self.sock = None
    def send(self, cmd):
        if not self.sock:
            self.sock = getSocket(addr, client_uuid)
            if not self.sock:
                print "Unable to open client socket"
                return
            print "Created socket"
        self.sock.sendall(cmd)
        if self.timer:
            self.timer.cancel()
            print "restarting timer"
        else:
            print "starting timer"
        self.timer = threading.Timer(5, self.disconnect)
        self.timer.start()
    def close(self):
        if self.timer:
            self.timer.cancel()
        self.disconnect()

def main():
    #server = AlarmServer()
    #server.start()
    
    sock = ClientSocket()

    cmd = ''
    while cmd not in ['q', 'quit', 'e', 'exit']:
        cmd = raw_input("^: ")

        if cmd == '':
            continue

        op, args = parse_cmd(cmd)

        if op in ['msg']: # send raw data to server
            sock.send(args + '\n')
        elif op in ['ack']: # acknowledge an active alarm
            sock.send(cmd + "\n")
        elif op in ['root']: # enable phone side administration
            sock.send(cmd + "\n")
        elif op in ['panic']: # initiate an alarm immediately
            sock.send(cmd)
        elif op in ['q', 'quit', 'e', 'exit']:
            pass
        else:
            print "Unknown command: %s" % (op)
    
    #server.stop()


if __name__ == '__main__':
    main()

