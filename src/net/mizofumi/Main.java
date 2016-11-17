package net.mizofumi;

public class Main {

    public static void main(String[] args) {

        //シリアル作成。(シリアルを操作した時や情報を受けた時に発動するイベントの定義をココで記述)
        Serial serial = new Serial(new SerialEventListener() {
            //データの読み出し時に呼ばれる
            @Override
            public void read(String data) {
                System.out.println(data);
            }

            //データの書き出し成功時に呼ばれる
            @Override
            public void write_success() {

            }

            //データの書き出し失敗時に呼ばれる
            @Override
            public void write_failed() {

            }

            //ポートの開放成功時に呼ばれる
            @Override
            public void open() {
                System.out.println("開放成功");
            }

            //ポートの開放失敗時に呼ばれる
            @Override
            public void open_failed(String errorMessage) {
                System.out.println("開放失敗"+errorMessage);
            }

            //停止時に呼ばれる
            @Override
            public void stop() {

            }

            //ポートの切断成功時に呼ばれる
            @Override
            public void close() {
                System.out.println("切断しました");
                System.exit(0); //プログラムを終了させる
            }

            @Override
            public void close_failed(String errorMessage) {
                System.out.println(errorMessage);
            }
        });


        serial.openPort("COM4"); //作成したシリアルのインスタンスを使って、実際にポート接続。
        serial.run(); //読み書きを監視するスレッドを開始

        //5秒間だけデータを読む
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        serial.stop(); //読み書きを監視するスレッドを停止
        serial.closePort(); //ポートを切断
    }
}
