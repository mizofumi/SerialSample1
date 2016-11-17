package net.mizofumi;

import gnu.io.*;

import java.io.*;
import java.util.TooManyListenersException;

/**
 * Created by mizof on 2016/11/17.
 */
public class Serial implements SerialPortEventListener{
    SerialEventListener listener;
    private SerialPort port;
    private boolean StopFlag = false;
    private BufferedReader reader;

    public Serial(SerialEventListener listener) {
        this.listener = listener;
    }

    public void openPort(String portName){
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName); // 例外要因1
            port = (SerialPort)portIdentifier.open("Main",2000); // 例外要因2
            port.setSerialPortParams( // 例外要因3
                    9600,
                    SerialPort.DATABITS_8, //データのビット数
                    SerialPort.STOPBITS_1, //ストップのビット
                    SerialPort.PARITY_NONE //パリティビット
            );
            port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            reader = new BufferedReader(
                    new InputStreamReader(port.getInputStream())); // 例外要因3

            port.addEventListener(this);
            port.notifyOnDataAvailable(true);
            listener.open();
        } catch (NoSuchPortException e) {// 例外要因1
            listener.open_failed("ポートが見つかりません");
        } catch (PortInUseException e) {// 例外要因2
            listener.open_failed("ポートが既に使用されています");
        } catch (UnsupportedCommOperationException e) { // 例外要因3
            listener.open_failed("ポートの設定に失敗しました");
        } catch (IOException e) {
            listener.open_failed("IOエラー");
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        }
    }

    public void closePort(){
        if (!StopFlag){
            listener.close_failed("読み書きスレッドが動作しています。stop()で止めて下さい。");
            return;
        }
        try {
            port.close();
            listener.close();
        }catch (NullPointerException e){
            listener.close_failed("閉じる対象のポートがありません");
        }
    }

    public void run(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStreamReader inputStreamReader = new InputStreamReader(System.in);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                try {
                    OutputStream outputStream = port.getOutputStream(); //例外要因1
                    while (!StopFlag){
                        String input = bufferedReader.readLine();
                        if (input.length() > 0){
                            /*
                            if (input.equals(":q")){ //  :qを受信したら強制終了
                                break;
                            }
                            */
                            input += "\r";
                            outputStream.write(input.getBytes("US-ASCII")); //OutputStreamに読んだデータを投げ込む
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void stop(){
        StopFlag = true;
        listener.stop();
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            String buffer = null;
            try {
                while (reader.ready()) {
                    buffer = reader.readLine();
                    listener.read(buffer);
                }
            } catch (IOException ex){
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }
}
