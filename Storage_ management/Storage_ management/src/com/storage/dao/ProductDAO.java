package com.storage.dao;

import com.storage.entity.ProductE;
import com.storage.utils.JDBC;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author MyPC
 */
public class ProductDAO extends StorageDAO<ProductE, String> {

    final String INSERT_SQL = "INSERT INTO PRODUCT (IDPRODUCT, PRODUCTNAME, IDTYPE, IDBRAND, PRICE, AMOUNT, TOTALMONEY, DATEADDED, IMG) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    final String UPDATE_SQL = "UPDATE PRODUCT SET PRODUCTNAME = ?, IDTYPE = ?, IDBRAND = ?, PRICE = ?, AMOUNT = ?, TOTALMONEY = ?, DATEADDED = ?, IMG = ? WHERE IDPRODUCT = ?";
    final String DELETE_SQL = "DELETE FROM PRODUCT WHERE IDPRODUCT = ?";
    final String SELECT_ALL_SQL = "SELECT * FROM PRODUCT";
    final String SELECT_BY_ID_SQL = "SELECT * FROM PRODUCT WHERE IDPRODUCT = ?";

    @Override
    public void insert(ProductE entity) {
        JDBC.update(INSERT_SQL, entity.getIdProduct(), entity.getProductName(), entity.getIdType(), entity.getIdBrand(), entity.getPrice(), entity.getAmount(), entity.getTotalMoney(), entity.getDateAdded(), entity.getImg());
    }

    @Override
    public void update(ProductE entity) {
        JDBC.update(UPDATE_SQL, entity.getProductName(), entity.getIdType(), entity.getIdBrand(), entity.getPrice(), entity.getAmount(), entity.getTotalMoney(), entity.getDateAdded(), entity.getImg(), entity.getIdProduct());
    }

    @Override
    public void delete(String key) {
        JDBC.update(DELETE_SQL, key);
    }

    @Override
    public List<ProductE> selectAll() {
        return this.selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public ProductE selectById(String key) {
        List<ProductE> list = this.selectBySql(SELECT_BY_ID_SQL, key);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    protected List<ProductE> selectBySql(String sql, Object... args) {
        List<ProductE> list = new ArrayList<ProductE>();
        try {
            ResultSet rs = JDBC.query(sql, args);
            while (rs.next()) {
                ProductE entity = new ProductE();
                entity.setIdProduct(rs.getString("IDPRODUCT"));
                entity.setProductName(rs.getString("PRODUCTNAME"));
                entity.setIdType(rs.getString("IDTYPE"));
                entity.setIdBrand(rs.getString("IDBRAND"));
                entity.setPrice(rs.getFloat("PRICE"));
                entity.setAmount(rs.getInt("AMOUNT"));
                entity.setTotalMoney(rs.getFloat("TOTALMONEY"));
                entity.setDateAdded(rs.getString("DATEADDED"));
                entity.setImg(rs.getString("IMG"));
                list.add(entity);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<ProductE> selectByKeyword(String keyword) {
        String sql = "SELECT * FROM PRODUCT WHERE PRODUCTNAME LIKE ?";
        return this.selectBySql(sql, '%' + keyword + "%");
    }

}
