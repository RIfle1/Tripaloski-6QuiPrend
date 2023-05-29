package project.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Address {
    private static List<InetAddress> availableDevices;
    private static String myNetworkIp;
    private static String deviceIpv4;

    public static List<InetAddress> returnAddresses(int port) {
        getDeviceIpv4();
        getMyNetworkIp();

        Thread thread = createScanningThreads(255, port);

        while (thread.isAlive()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return availableDevices;
    }

    public static void getMyNetworkIp() {
        for (int i = deviceIpv4.length() - 1; i >= 0; --i) {
            if (deviceIpv4.charAt(i) == '.') {
                myNetworkIp = deviceIpv4.substring(0, i + 1);
                break;
            }
        }
    }

    public static void getDeviceIpv4() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            deviceIpv4 = localhost.getHostAddress();

            if (deviceIpv4.equals("127.0.0.1")) {
                System.out.println("This PC is not connected to any network!");
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static Thread createScanningThreads(int threads, int port) {
        Thread thread = null;
        availableDevices = new ArrayList<>();

        for (int i = 0; i < threads; ++i) {
            int start = i * 256 / threads;
            int end = (i + 1) * 256 / threads - 1;

            thread = new Thread(() -> scanDevices(start, end, port));
            thread.start();
        }

        return thread;
    }

    public static void scanDevices(int start, int end, int port) {

        for (int i = start; i <= end; ++i) {
            try {
                InetAddress addr = InetAddress.getByName(myNetworkIp + i);
                if (addr.isReachable(1000)) {
                    try {
                        Socket socket = new Socket(addr.getHostAddress(), port);
                        socket.close();
                        availableDevices.add(addr);
                    } catch (IOException e) {
                        System.out.println("No server on " + addr.getHostAddress() + ":" + port);
                    }
                }

            } catch (IOException ignored) {}
        }
    }

}
