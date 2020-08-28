package bm.demo.blockchain;

import itx.examples.blockchain.advanced.Block;
import itx.examples.blockchain.advanced.BlockChainUtils;
import itx.examples.blockchain.advanced.Ledger;
import itx.examples.blockchain.advanced.LedgerBuilder;

import java.util.ArrayList;
import java.util.List;

public class BlockChainDemo {

    public static void main(String[] args) {
        System.out.println("BLOCK CHAIN DEMO");
        Ledger ledger;
        BlockChainDemo demo = new BlockChainDemo();

        System.out.println("CREATING A BLOCK CHAIN") ;
        ledger = demo.createLedger();

        System.out.println("LISTING CREATED BLOCK CHAIN") ;
        demo.showLedger(ledger);

        System.out.println(String.format("VERIFYING THE BLOCK CHAIN (ID:%s) : ", ledger.getId()));
        if (demo.verifyLedger(ledger)) {
            System.err.println("[PASS] This block chain verified successfully");
        } else {
            System.out.println("[FAIL] This block has been tampered");
        }

        int tamper_blkidx = 2;
        String tamper_data = "License $893.01 <-- Tampered here !!!";
        ledger = demo.tamperLedger(ledger, tamper_blkidx, tamper_data);

        System.out.println("LISTING TEMPERED BLOCK CHAIN") ;
        demo.showLedger(ledger);

        if (demo.verifyLedger(ledger)) {
            System.err.println("[FAIL] The tampered block chain not failed to pass verification");
        } else {
            System.out.println("[PASS] The tampered block chain failed to pass verification successfully");
        }
    }

    public Ledger createLedger() {
        LedgerBuilder ledgerBuilder = new LedgerBuilder();
        ledgerBuilder.setId("ledger expenditure");
        ledgerBuilder.setHashPrefix("00");
        ledgerBuilder.addData("Books $1435.00");
        ledgerBuilder.addData("Travel $28374.32");
        ledgerBuilder.addData("License $893.00");
        ledgerBuilder.addData("Equipment $938.99");
        ledgerBuilder.addData("Services $6893.43");
        ledgerBuilder.addData("Vehicles $3847.40");
        return ledgerBuilder.build();
    }

    public void showLedger(Ledger ledger) {
        for (Block block: ledger.getBlocks()) {
            System.out.println(String.format("Block #%s: %s", block.getId(), block.getData())) ;
        }
    }

    public boolean verifyLedger(Ledger ledger) {
        boolean retval = true;

        System.out.print("Verifying the blocks in the block chain");
        for (Block block: ledger.getBlocks()) {
            boolean blockOk = BlockChainUtils.verifyBlock(block, ledger.getHashPrefix());
            System.out.print(String.format("Verifying block #%s: ", block.getId()));
            if (!blockOk) {
                System.err.println("FAIL");
                retval = false;
            } else {
                System.out.println("PASS");
            }
        }

        System.out.println("Verifying the whole block chain");
        return retval && BlockChainUtils.verifyLedger(ledger);
    }

    public Ledger tamperLedger(Ledger ledger, int blkidx, String blkdata) {
        System.out.println(String.format("TAMPERING THE BLOCK CHAIN (ID:%s)",ledger.getId()));

        Block block = ledger.getBlockAt(blkidx);
        System.out.println(String.format("Tampering a block #%s with data: %s",block.getId(), block.getData()));

        Block tamperedBlock = new Block(block.getId(), block.getNonce(), blkdata, block.getPreviousHash(), block.getHash());
        System.out.println(String.format("Tampered the block #%s with data: %s",tamperedBlock.getId(), tamperedBlock.getData()));

        List<Block> tamperedList = new ArrayList<Block>(ledger.getBlocks());
        tamperedList.set(blkidx, tamperedBlock);
        return new Ledger(ledger.getId(), ledger.getHashPrefix(), tamperedList);
    }

}
