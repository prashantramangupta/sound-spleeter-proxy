package io.singularitylab.soundspleeter;

import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import io.singularitynet.service.soundspleeter.SoundSpleeterGrpc;
import io.singularitynet.service.soundspleeter.SoundSpleeterGrpc.SoundSpleeterBlockingStub;
import io.singularitynet.service.soundspleeter.SoundSpleeterOuterClass.Input;
import io.singularitynet.service.soundspleeter.SoundSpleeterOuterClass.Output;

public class Client {

    public static void main(String[] args) throws Exception {
        if (args.length != 5) {
            System.out.println("Usage: Client <proxy_host> <proxy_port> <audio_url> <vocals.wav> <accomp.wav>");
            System.exit(1);
        }

        String proxyHost = args[0];
        int proxyPort = Integer.parseInt(args[1]);
        String audioUrl = args[2];
        String vocalsWav = args[3];
        String accompWav = args[4];

        ManagedChannel channel = ManagedChannelBuilder
            .forAddress(proxyHost, proxyPort)
            .maxInboundMessageSize(1 << 24)
            .usePlaintext()
            .build();
        try {
            SoundSpleeterBlockingStub stub = SoundSpleeterGrpc.newBlockingStub(channel);

            Input request = Input.newBuilder()
                .setAudioUrl(audioUrl)
                .build();
            Output response = stub.spleeter(request);
            bytesToFile(response.getVocals().toByteArray(), vocalsWav);
            bytesToFile(response.getAccomp().toByteArray(), accompWav);
        } finally {
            channel.shutdownNow();
        }
    }

    private static void bytesToFile(byte[] bytes, String fileName) throws IOException, FileNotFoundException {
        FileOutputStream stream = new FileOutputStream(fileName);
        try {
            stream.write(bytes);
        } finally {
            stream.close();
        }
    }

}