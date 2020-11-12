package com.bill.zografos;

import com.google.gson.GsonBuilder;
import java.util.Date;
import java.util.UUID;

/**
 * Created by vasilis on 01/12/2019.
 */
public class Block {
    private String id;
    private String hash;
    private String previousHash;
    private String data; //our data will be a simple message.
    private long timestamp; //as number of milliseconds since 1/1/1970.
    private String nonce; //random unique id for mining

    private Atomic<Boolean> nonceFound = new Atomic<>(false);
    private Lock lock = new Lock();
    private Lock lock2 = new Lock();

    public Block(String data, String previousHash) {
        this.id = UUID.randomUUID().toString();
        this.data = data;
        this.previousHash = previousHash;
        this.timestamp = new Date().getTime();
        this.nonce = UUID.randomUUID().toString();
        this.hash = calculateHash(); // Making sure we do this after we set the other values.
    }

    public String getId() { return id; }

    public long getTimestamp() { return timestamp; }

    public String getData() { return data; }

    public String getNonce() { return nonce; }

    public String getHash() { return hash; }

    public String getPreviousHash() {
        return this.previousHash;
    }

    public String calculateHash() {
        String calculatedhash = StringUtil.applySha256(
                previousHash + Long.toString(timestamp) + nonce + data
        );
        return calculatedhash;
    }

    public void mineBlock(int difficulty) {
        if (nonceFound.get()) nonceFound.set(false); // reset condition

        Runnable code = () -> {
            String _data;
            String _hash;
            String _prevHash;
            String _nonce;
            long _timestamp;
            int zeros;

            // copy shared values
            lock.lock();
            _timestamp = this.timestamp;
            _nonce = this.nonce;
            _prevHash = this.previousHash;
            _data = this.data;
            _hash = this.hash;
            zeros = difficulty;
            lock.unlock();

            String target = new String(new char[zeros]).replace('\0', '0');
            while (!_hash.substring(0, zeros).equals(target) && !nonceFound.get()) {
                _nonce = UUID.randomUUID().toString();
                _hash = StringUtil.applySha256(
                        _prevHash + Long.toString(_timestamp) + _nonce + _data
                );
            }

            lock2.lock();
            if (!nonceFound.get()) {
                this.nonce = _nonce;
                this.hash = _hash;
                nonceFound.set(true);
            }
            lock2.unlock();
        };

        for (int i = 0; i < 8; i++) {
            Thread thread = new Thread(code);
            thread.start();
        }

        Long startTime = System.nanoTime();
        while (!nonceFound.get()) {/* wait */}
        Long endTime = System.nanoTime();

        float time = (float) ((endTime - startTime) / 1000000000); // (end - start) / 10 ^ -9
        System.out.println("mining finished in: "+time+" secs\n");
    }

    public String toJson() {
        String blockJson = new GsonBuilder().setPrettyPrinting().create().toJson(this);
        return blockJson;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
