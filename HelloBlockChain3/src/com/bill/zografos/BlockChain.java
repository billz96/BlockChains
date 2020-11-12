package com.bill.zografos;

import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by vasilis on 01/12/2019.
 */
public class BlockChain {
    protected List<Block> chain;
    protected int difficulty;

    public BlockChain(int difficulty) {
        this.chain = new ArrayList<>();
        this.difficulty = difficulty;
    }

    public Block firstBlock() {
        return this.chain.get(0);
    }

    public Block lastBlock() {
        return this.chain.get(chain.size()-1);
    }

    public void add(String data) {
        Block block;

        if (chain.size() > 0) {
            block = new Block(data, lastBlock().getHash());
        } else {
            block = new Block(data, UUID.randomUUID().toString());
        }

        block.mineBlock(difficulty);
        chain.add(block);

        // show block
        Block _lastBlock = lastBlock();
        System.out.println("Block-ID: "+_lastBlock.getId());
        System.out.println("Hash: "+_lastBlock.getHash());
        System.out.println("PrevHash: "+_lastBlock.getPreviousHash());
        System.out.println("Timestamp: "+_lastBlock.getTimestamp());
        System.out.println("Nonce: "+_lastBlock.getNonce()+"\n");
    }

    public void pprint() {
        for (int i = 0; i < chain.size(); i++) {
            System.out.print("com.bill.zografos.Block #"+i+":"+"\n");
            System.out.print("  Hash: "+chain.get(i).getHash()+"\n");
            System.out.print("  Previous Hash: "+chain.get(i).getHash()+"\n");
            System.out.print("  Nonce: "+chain.get(i).getNonce()+"\n");
            System.out.print("  Timestamp: "+chain.get(i).getTimestamp()+"\n");
            System.out.println("  Data: "+chain.get(i).getData()+"\n");
        }
    }

    public String toJson() {
        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(this);
        return blockchainJson;
    }

    public Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;

        //loop through blockchain to check hashes:
        for(int i=1; i < this.chain.size(); i++) {
            currentBlock = this.chain.get(i);
            previousBlock = this.chain.get(i-1);

            //compare registered hash and calculated hash:
            if(!currentBlock.getHash().equals(currentBlock.calculateHash())){
                System.out.println("Current Hashes not equal");
                return false;
            }

            //compare previous hash and registered previous hash
            if(!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
        }

        return true;
    }
}
