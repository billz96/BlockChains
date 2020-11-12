package com.bill.zografos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vasilis on 01/12/2019.
 */
public class ProductManager extends BlockChain {
    public ProductManager(int difficulty) {
        super(difficulty);
        Connection conn = ProductManager.createConnection();
        try {
            String table = "CREATE TABLE IF NOT EXISTS ProductBlocks (\n" +
                    "\t\"id\"\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\t\"name\"\tTEXT,\n" +
                    "\t\"description\"\tTEXT,\n" +
                    "\t\"prevID\"\tINTEGER,\n" +
                    "\t\"hash\"\tTEXT,\n" +
                    "\t\"prevHash\"\tTEXT,\n" +
                    "\t\"productTimestamp\"\tTIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                    "\t\"category\"\tTEXT,\n" +
                    "\t\"blockTimestamp\"\tTEXT,\n" +
                    "\t\"barcode\"\tTEXT,\n" +
                    "\t\"price\"\tREAL,\n" +
                    "\t\"nonce\"\tTEXT,\n" +
                    "\t\"blockID\"\tTEXT\n" +
                    ")";

            conn.setAutoCommit(false);

            Statement stmt = conn.createStatement();

            // create table if it doesn't exist
            stmt.execute(table);

            // check if we've already have entries in our database
            String sql = "SELECT * FROM ProductBlocks WHERE id = (SELECT MAX(id) FROM ProductBlocks ORDER BY id ASC)";
            ResultSet rs = stmt.executeQuery(sql);
            if (!rs.next()) {
                // create our first *real* genesis block with some fake data
                Product product = new Product("dummy product", "", 0, "", "");
                this.add(product.toJson()); // add and mine genesis block

                // block data
                Block genesisBlock = this.firstBlock();
                String hash = genesisBlock.getHash();
                String prevHash = genesisBlock.getPreviousHash();
                long blockTimestamp = genesisBlock.getTimestamp();
                String nonce = genesisBlock.getNonce();
                String blockID = genesisBlock.getId();

                // product data
                String name = product.getName();
                String barcode = product.getBarcode();
                float price = product.getPrice();
                String category = product.getCategory();
                String description = product.getDescription();

                // add block & product to database
                String sql2 = "INSERT INTO ProductBlocks (name, barcode, category, price, description, hash, prevHash, blockTimestamp, nonce, blockID) " +
                        "VALUES ('"+name+"','"+barcode+"','"+category+"',"+price+",'"+description+"','"+hash+"','"+prevHash+"','"+blockTimestamp+"','"+nonce+"','"+blockID+"');";
                stmt.executeUpdate(sql2);
                conn.commit();

                rs.close();
                stmt.close();
                conn.close();
            } else {
                // create a new genesis block with the latest data in order to extend the chain
                String hash = rs.getString("hash");
                String prevHash = rs.getString("prevHash");
                String nonce = rs.getString("nonce");
                long blockTimestamp = Long.decode(rs.getString("blockTimestamp"));

                // get product's data
                String name = rs.getString("name");
                String barcode = rs.getString("barcode");
                float price = rs.getFloat("price");
                String category = rs.getString("category");
                String description = rs.getString("description");

                // block's data property value
                String data = new Product(name, barcode, price, category, description).toJson();

                Block genesisBlock = new Block("", prevHash);
                genesisBlock.setNonce(nonce);
                genesisBlock.setTimestamp(blockTimestamp);
                genesisBlock.setHash(hash);
                genesisBlock.setData(data);

                this.chain.add(genesisBlock);

                rs.close();
                stmt.close();
                conn.close();
            }

        } catch (SQLException ex) {
            System.out.print(ex.getClass().getName()+": "+ex.getMessage()+"\n");
        }
    }

    public static Connection createConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:HelloBlockChain3.sqlite");
            System.out.println("Opened database successfully!");
            return conn;
        } catch (Exception ex) {
            System.out.print(ex.getClass().getName()+": "+ex.getMessage()+"\n");
        }
        return null;
    }

    public String findProducts() {
        Connection conn = ProductManager.createConnection();
        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT DISTINCT name FROM ProductBlocks WHERE name != 'dummy product';";
            ResultSet rs = stmt.executeQuery(sql);

            // get product names
            List<String> names = new ArrayList<>();
            while (rs.next()) {
                String name = rs.getString(1);
                names.add(name);
            }

            List<String> results = new ArrayList<>();
            for (String name : names) {
                if (!(results.indexOf(name) >= 0)) {
                    results.add(name);
                }
            }

            String finalResults = "";
            for (String result : results) {
                finalResults += result + " ";
            }

            return finalResults;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "none";
    }

    public void addProduct(Product product) {
        // Add product to chain and then add the block to database
        this.add(product.toJson()); // add and mine block
        Block block = this.lastBlock(); // get mined block

        // get block's hashes, timestamp and nonce
        String hash = block.getHash();
        String prevHash = block.getPreviousHash();
        long blockTimestamp = block.getTimestamp();
        String nonce = block.getNonce();
        String blockID = block.getId();

        // get product's data
        String name = product.getName();
        String barcode = product.getBarcode();
        String category = product.getCategory();
        String description = product.getDescription();
        float price = product.getPrice();

        Connection conn = ProductManager.createConnection();
        try {
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();

            String sql = "INSERT INTO ProductBlocks (name, barcode, category, price, description, hash, prevHash, blockTimestamp, nonce, blockID) " +
                    "VALUES ('"+name+"','"+barcode+"','"+category+"',"+price+",'"+description+"','"+hash+"','"+prevHash+"','"+blockTimestamp+"','"+nonce+"','"+blockID+"');";
            stmt.executeUpdate(sql);
            conn.commit();

            String sql2 = "SELECT MAX(id) FROM ProductBlocks WHERE name = '"+name+"' AND blockID != '"+blockID+"' ORDER BY id ASC;";
            ResultSet rs2 = stmt.executeQuery(sql2);
            if (rs2.next()) {
                int maxID = rs2.getInt(1);
                String sql3 = "UPDATE ProductBlocks \n" +
                        "SET prevID = '"+maxID+"'\n" +
                        "WHERE blockID = '"+blockID+"';";

                stmt.executeUpdate(sql3);
                conn.commit();
            }

            rs2.close();
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            System.out.print(ex.getClass().getName()+": "+ex.getMessage()+"\n");
        }
    }

    public void addProducts(List<Product> products) {
        for (Product product : products) {
            this.addProduct(product);
        }
    }

    public String pricesOfProduct(String name) {
        Connection conn = ProductManager.createConnection();
        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM ProductBlocks WHERE name = '"+name+"' ORDER BY id ASC;";
            ResultSet rs = stmt.executeQuery(sql);

            String result = "";
            int count = 0;
            while (rs.next()) {
                count++;
                float price = rs.getFloat("price");
                result += "\n\t"+name.toLowerCase()+"-price #"+count+" "+price+"\n";
            }

            rs.close();
            stmt.close();
            conn.close();
            return result;
        } catch (SQLException ex) {
            System.out.print(ex.getClass().getName()+": "+ex.getMessage()+"\n");
        }
        return "none";
    }

    public String findProduct(String name) {
        Connection conn = ProductManager.createConnection();
        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM ProductBlocks WHERE name = '"+name+"' AND id = (SELECT MAX(id) FROM ProductBlocks WHERE name = '"+name+"')";
            ResultSet rs = stmt.executeQuery(sql);

            // get entry's data
            int id = rs.getInt("id");
            String barcode = rs.getString("barcode");
            String category = rs.getString("category");
            String description = rs.getString("description");
            float price = rs.getFloat("price");
            rs.close();

            // result's data
            String result = "";
            result = "com.bill.zografos.Product #" + barcode + "\n";
            result += "   id: " + id + "\n";
            result += "   name: " + name + "\n";
            result += "   price: " + price + "\n";
            result += "   category: " + category + "\n";
            result += "   description: " + description + "\n";

            String sql2 = "SELECT COUNT(id) FROM ProductBlocks WHERE name = '"+name+"';";
            ResultSet rs2 = stmt.executeQuery(sql2);
            int count = rs2.getInt(1);
            result += "   entry number: " + count + "\n";
            rs2.close();

            stmt.close();
            conn.close();
            return result;
        } catch (SQLException ex) {
            System.out.print(ex.getClass().getName()+": "+ex.getMessage()+"\n");
        }

        return "none";
    }

    public String findProductsByCategory(String category) {
        Connection conn = ProductManager.createConnection();
        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT DISTINCT name FROM ProductBlocks WHERE category = '"+category+"' ORDER BY name ASC;";

            ResultSet rs = stmt.executeQuery(sql);
            String products = "";
            while (rs.next()) {
                products += rs.getString("name")+" ";
            }

            rs.close();
            stmt.close();
            conn.close();
            return products;

        } catch (SQLException ex) {
            System.out.print(ex.getClass().getName()+": "+ex.getMessage()+"\n");
        }
        return "none";
    }

    public String getCategories() {
        Connection conn = ProductManager.createConnection();
        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT DISTINCT category FROM ProductBlocks WHERE category != '' ORDER BY category ASC;";

            ResultSet rs = stmt.executeQuery(sql);
            String categories = "";
            while (rs.next()) {
                categories += rs.getString("category")+" ";
            }

            rs.close();
            stmt.close();
            conn.close();
            return categories;

        } catch (SQLException ex) {
            System.out.print(ex.getClass().getName()+": "+ex.getMessage()+"\n");
        }
        return "none";
    }
}
