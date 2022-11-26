package com.storage.ui;

import com.storage.dao.BrandDAO;
import com.storage.dao.ProductDAO;
import com.storage.dao.TypeDAO;
import com.storage.entity.BrandE;
import com.storage.entity.ProductE;
import com.storage.entity.TypeE;
import com.storage.utils.Auth;
import com.storage.utils.MsgBox;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author MyPC
 */
public class StorageImport extends javax.swing.JFrame {

    ProductDAO pddao = new ProductDAO();
    TypeDAO tdao = new TypeDAO();
    BrandDAO bdao = new BrandDAO();
    long millis = System.currentTimeMillis();
    java.sql.Date day = new java.sql.Date(millis);
    int row = -1;
    List<ProductE> listEx = null;
    private String imageSrc = "SideRight.png";
    String fileimg;

    /**
     * Creates new form Import
     */
    public StorageImport() {
        initComponents();
        initData();
    }

    public void initData() {
        setUser();
        Clock();
        fillCboType();
        fillCboBrand();
        fillDataToTable();
    }

    public void setUser() {
        lblUser.setText(Auth.user.getFullName());
        Boolean position = Auth.user.isPosition();
        String Position = "";
        if (position == true) {
            Position = "Manager";
        } else {
            Position = "Employee";
        }
        lblPosition.setText(Position);
    }

    public void fillCboType() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cboType.getModel();
        model.removeAllElements();
        try {
            List<TypeE> list = tdao.selectAll();
            for (TypeE t : list) {
                model.addElement(t.getType());
            }
        } catch (Exception e) {
            MsgBox.alert(this, "Lỗi truy vấn dữ liệu!");
        }
    }

    public void fillCboBrand() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cboBrand.getModel();
        model.removeAllElements();
        try {
            List<BrandE> list = bdao.selectAll();
            for (BrandE b : list) {
                model.addElement(b.getBrand());
            }
        } catch (Exception e) {
            MsgBox.alert(this, "Lỗi truy vấn dữ liệu!");
        }
    }

    public void fillDataToTable() {
        TypeDAO tdaof = new TypeDAO();
        BrandDAO bdaof = new BrandDAO();
        DefaultTableModel model = (DefaultTableModel) tblListData.getModel();
        model.setRowCount(0);
        try {
            //listPD.clear();
            String keyword = txtSearch.getText();
            List<ProductE> list = pddao.selectByKeyword(keyword);
            listEx = list;
            for (ProductE pd : list) {
                TypeE t = tdaof.selectById(pd.getIdType());
                BrandE b = bdaof.selectById(pd.getIdBrand());
                Object[] row = {pd.getIdProduct(), pd.getProductName(), t.getType(), b.getBrand(), pd.getPrice(), pd.getAmount(), pd.getTotalMoney(), pd.getDateAdded()};
                model.addRow(row);
            }
        } catch (Exception e) {
            MsgBox.alert(this, "Lỗi truy vấn dữ liệu T!");
        }
    }

    public void setDataToForm(ProductE pd) {
        txtIDProduct.setText(pd.getIdProduct());
        txtProductName.setText(pd.getProductName());
        TypeE t = tdao.selectById(pd.getIdType());
        BrandE b = bdao.selectById(pd.getIdBrand());
        if (t == null) {
            cboType.setSelectedIndex(0);
            cboBrand.setSelectedIndex(0);
        } else {
            cboType.setSelectedItem(t.getType());
            cboBrand.setSelectedItem(b.getBrand());
        }
        txtAmount.setText(String.valueOf(pd.getAmount()));
        txtPrice.setText(String.valueOf(pd.getPrice()));
        if (pd.getImg() != null) {
            File file = new File("src\\com\\storage\\image\\" + pd.getImg());
            try {
                Image img = ImageIO.read(file);
                int width = lblImage.getWidth();
                int height = lblImage.getHeight();
                lblImage.setIcon(new ImageIcon(img.getScaledInstance(width, height, 0)));
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public ProductE getDataForm() {
        ProductE pd = new ProductE();
        pd.setIdProduct(txtIDProduct.getText());
        pd.setProductName(txtProductName.getText());
        List<TypeE> listType = tdao.selectAll();
        String type = cboType.getSelectedItem().toString();
        for (TypeE ty : listType) {
            if (type.equals(ty.getType())) {
                pd.setIdType(String.valueOf(ty.getIdType()));
            }
        }
        String brand = cboBrand.getSelectedItem().toString();
        List<BrandE> listBrand = bdao.selectAll();
        for (BrandE b : listBrand) {
            if (brand.equals(b.getBrand())) {
                pd.setIdBrand(String.valueOf(b.getIdBrand()));
            }
        }
        pd.setPrice(Float.valueOf(txtPrice.getText()));
        pd.setAmount(Integer.valueOf(txtAmount.getText()));
        float ttm = Float.valueOf(txtPrice.getText()) * Integer.valueOf(txtAmount.getText());
        pd.setTotalMoney(ttm);
        pd.setDateAdded(String.valueOf(day));
        pd.setImg(imageSrc);
        return pd;
    }

    public void clearForm() {
        ProductE pd = new ProductE();
        setDataToForm(pd);
        row = -1;
        imageSrc = "SideRight.png";
        File file = new File("src\\com\\storage\\image\\" + imageSrc);
        try {
            Image img = ImageIO.read(file);
            int width = lblImage.getWidth();
            int height = lblImage.getHeight();
            lblImage.setIcon(new ImageIcon(img.getScaledInstance(width, height, 0)));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public boolean check() {
        if (txtIDProduct.getText().equals("")) {
            MsgBox.alert(this, "Id product is empty!");
            txtIDProduct.requestFocus();
            return false;
        }
        if (txtProductName.getText().equals("")) {
            MsgBox.alert(this, "Product name is empty!");
            txtProductName.requestFocus();
            return false;
        }
        if (txtAmount.getText().equals("")) {
            MsgBox.alert(this, "Amount is empty!");
            txtAmount.requestFocus();
            return false;
        } else {
            try {
                int test = Integer.valueOf(txtAmount.getText());
            } catch (Exception ex) {
                MsgBox.alert(this, "Amount have to be a number");
                txtAmount.requestFocus();
                return false;
            }
        }
        if (txtPrice.getText().equals("")) {
            MsgBox.alert(this, "Price is empty!");
            txtPrice.requestFocus();
            return false;
        } else {
            try {
                float test = Float.valueOf(txtPrice.getText());
            } catch (Exception ex) {
                MsgBox.alert(this, "Price have to be a number");
                txtPrice.requestFocus();
                return false;
            }
        }
        return true;
    }

    public void edit() {
        String idpd = (String) tblListData.getValueAt(this.row, 0);
        tblListData.setRowSelectionInterval(row, row);
        ProductE pd = pddao.selectById(idpd);
        setDataToForm(pd);
        lblRecord.setText(setRecord());
    }

    public void save() {
        try {
            copyImage();
            String key = txtIDProduct.getText();
            ProductE pd = pddao.selectById(key);
            if (pd == null) {
                ProductE pdInsert = getDataForm();
                pddao.insert(pdInsert);
            } else {
                if (MsgBox.confirm(this, "Update this product?")) {
                    ProductE pdUpdate = getDataForm();
                    pddao.update(pdUpdate);
                    clearForm();
                    fillDataToTable();
                    MsgBox.alert(this, "Save success!");
                }
            }
        } catch (Exception e) {
            MsgBox.alert(this, "Save failed!");
            System.out.println(e);
        }
    }

    public void delete() {
        if (!Auth.isManager()) {
            MsgBox.alert(this, "You aren't manager!");
        } else {
            if (MsgBox.confirm(this, "Delete this product?")) {
                try {
                    String idpd = txtIDProduct.getText();
                    pddao.delete(idpd);
                    fillDataToTable();
                    clearForm();
                    MsgBox.alert(this, "Delete Success!");
                } catch (Exception e) {

                }
            }
        }
    }

    public void search() {
        fillDataToTable();
        clearForm();
        row = - 1;
    }

    public void first() {
        row = 0;
        edit();
    }

    public void prev() {
        row--;
        if (row < 0) {
            last();
        }
        edit();
    }

    public void next() {
        row++;
        if (row > tblListData.getRowCount() - 1) {
            first();
        }
        edit();
    }

    public void last() {
        row = tblListData.getRowCount() - 1;
        edit();
    }

    public String setRecord() {
        String keyword = txtSearch.getText();
        List<ProductE> list = pddao.selectByKeyword(keyword);
        return (row + 1) + " of " + list.size();
    }

    public void Sort() {
        String Sortby = String.valueOf(cboSort.getSelectedItem());
        List<ProductE> list = pddao.selectAll();
        if (Sortby.equals("ID Product")) {
            Comparator<ProductE> com = new Comparator<ProductE>() {
                @Override
                public int compare(ProductE o1, ProductE o2) {
                    return o1.idProduct.compareTo(o2.idProduct);
                }
            };
            Collections.sort(list, com);
        }
        if (Sortby.equals("Name")) {
            Comparator<ProductE> com = new Comparator<ProductE>() {
                @Override
                public int compare(ProductE o1, ProductE o2) {
                    return o1.productName.compareTo(o2.productName);
                }
            };
            Collections.sort(list, com);
        }
        if (Sortby.equals("Type")) {
            Comparator<ProductE> com = new Comparator<ProductE>() {
                @Override
                public int compare(ProductE o1, ProductE o2) {
                    return o1.idType.compareTo(o2.idType);
                }
            };
            Collections.sort(list, com);
        }
        if (Sortby.equals("Brand")) {
            Comparator<ProductE> com = new Comparator<ProductE>() {
                @Override
                public int compare(ProductE o1, ProductE o2) {
                    return o1.idBrand.compareTo(o2.idBrand);
                }
            };
            Collections.sort(list, com);
        }
        if (Sortby.equals("Price")) {
            Comparator<ProductE> com = new Comparator<ProductE>() {
                @Override
                public int compare(ProductE o1, ProductE o2) {
                    return Float.compare(o2.getPrice(), o1.getPrice());
                }
            };
            Collections.sort(list, com);
        }

        fillDataToTableSort(list);
    }

    public void fillDataToTableSort(List list) {
        TypeDAO tdaof = new TypeDAO();
        BrandDAO bdaof = new BrandDAO();
        DefaultTableModel model = (DefaultTableModel) tblListData.getModel();
        model.setRowCount(0);
        try {
            List<ProductE> listSort = list;
            listEx.clear();
            listEx = list;
            for (ProductE pd : listSort) {
                TypeE t = tdaof.selectById(pd.getIdType());
                BrandE b = bdaof.selectById(pd.getIdBrand());
                Object[] row = {pd.getIdProduct(), pd.getProductName(), t.getType(), b.getBrand(), pd.getPrice(), pd.getAmount(), pd.getTotalMoney(), pd.getDateAdded()};
                model.addRow(row);
            }
        } catch (Exception e) {
            MsgBox.alert(this, "Lỗi truy vấn dữ liệu T!");
        }
    }

    public void ExportFileExcel() {
        try {
            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet sheet = wb.createSheet("Product list");
            XSSFRow row = null;
            Cell cell = null;
            row = sheet.createRow(1);

            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue("ID Product");

            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue("Product name");

            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue("Type");

            cell = row.createCell(4, CellType.STRING);
            cell.setCellValue("Brand");

            cell = row.createCell(5, CellType.STRING);
            cell.setCellValue("Price");

            cell = row.createCell(6, CellType.STRING);
            cell.setCellValue("Amount");

            cell = row.createCell(7, CellType.STRING);
            cell.setCellValue("Total money");

            cell = row.createCell(8, CellType.STRING);
            cell.setCellValue("Data added");

            int i = 1;
            for (ProductE item : listEx) {

                row = sheet.createRow(i + 1);

                cell = row.createCell(1, CellType.STRING);
                cell.setCellValue(String.valueOf(item.getIdProduct()));

                cell = row.createCell(2, CellType.STRING);
                cell.setCellValue(String.valueOf(item.getProductName()));

                TypeDAO tdaof = new TypeDAO();
                BrandDAO bdaof = new BrandDAO();

                TypeE t = tdaof.selectById(item.getIdType());
                BrandE b = bdaof.selectById(item.getIdBrand());

                cell = row.createCell(3, CellType.STRING);
                cell.setCellValue(String.valueOf(t.getType()));

                cell = row.createCell(4, CellType.STRING);
                cell.setCellValue(String.valueOf(b.getBrand()));

                cell = row.createCell(5, CellType.STRING);
                cell.setCellValue(String.valueOf(item.getPrice()));

                cell = row.createCell(6, CellType.STRING);
                cell.setCellValue(String.valueOf(item.getAmount()));

                cell = row.createCell(7, CellType.STRING);
                cell.setCellValue(String.valueOf(item.getTotalMoney()));

                cell = row.createCell(8, CellType.STRING);
                cell.setCellValue(String.valueOf(item.getDateAdded()));

                i++;
            }

            JFileChooser fc = new JFileChooser();
            fc.showOpenDialog(null);
            File f = fc.getSelectedFile();
            String path = f.getAbsoluteFile().toString();
            String file = f.getAbsolutePath();
            if (!path.contains(".xlsx")) {
                file = f.getAbsolutePath() + ".xlsx";
            }
            try {
                FileOutputStream fis = new FileOutputStream(file);
                wb.write(fis);
                fis.close();
                MsgBox.alert(this, "Export success!");
            } catch (Exception ex) {
                System.out.println("Export error " + ex);
            }

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public void chooseImage() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.showOpenDialog(null);
            File file = chooser.getSelectedFile();
            if (file == null) {
                return;
            }
            fileimg = file.getParent();
            imageSrc = file.getName();
            Image img = ImageIO.read(file);
            int width = lblImage.getWidth();
            int height = lblImage.getHeight();
            lblImage.setIcon(new ImageIcon(img.getScaledInstance(width, height, 0)));
        } catch (Exception e) {
        }
    }

    public void copyImage() {
        try {
            List<ProductE> list = pddao.selectAll();
            String checkExistImg = "";
            for (ProductE pd : list) {
                if (pd.getImg().equals(imageSrc)) {
                    checkExistImg = "already";
                }
            }
            if (checkExistImg.equals("already")) {
                //System.out.println("dont copy");
            } else {
                File sourceFolder = new File(fileimg + "\\" + imageSrc);
                File targetFolder = new File("src\\com\\storage\\image\\" + imageSrc);
                copyFolder(sourceFolder, targetFolder);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void copyFolder(File sourceFolder, File targetFolder)
            throws IOException {
        InputStream in = new FileInputStream(sourceFolder);
        OutputStream out = new FileOutputStream(targetFolder);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
        in.close();
        out.close();
    }

    void Clock() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Date now = new Date();
                        SimpleDateFormat formater = new SimpleDateFormat();
                        formater.applyPattern("hh:mm:ss aa");
                        String time = formater.format(now);
                        lblClock.setText(time);
                        SimpleDateFormat formater2 = new SimpleDateFormat();
                        formater2.applyPattern("dd-MM-yyyy");
                        String dayt = formater2.format(day);
                        lblToday.setText(String.valueOf(dayt));
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }

                }
            }
        }).start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cardContainer = new javax.swing.JPanel();
        cardStorage = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        txtIDProduct = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtProductName = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        cboType = new javax.swing.JComboBox<>();
        txtAmount = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        btnNew = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        lblUser = new javax.swing.JLabel();
        lblPosition = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        cboBrand = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        txtPrice = new javax.swing.JFormattedTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblToday = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblClock = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblListData = new javax.swing.JTable();
        lblImage = new javax.swing.JLabel();
        lblRecord = new javax.swing.JLabel();
        btnExport = new javax.swing.JButton();
        cboSort = new javax.swing.JComboBox<>();
        btnLast = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnFirst = new javax.swing.JButton();
        btnPrev = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        lblHome = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        lblAccount = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MoonFlower");
        getContentPane().setLayout(new java.awt.CardLayout());

        cardContainer.setLayout(new java.awt.CardLayout());

        cardStorage.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(0, 51, 51));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 308, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 38, Short.MAX_VALUE)
        );

        cardStorage.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 530, 310, 40));

        jPanel2.setBackground(new java.awt.Color(255, 204, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        txtSearch.setBackground(new java.awt.Color(255, 204, 204));
        txtSearch.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "Search", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));
        txtSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtSearch.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtSearch.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtSearchCaretUpdate(evt);
            }
        });
        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSearchKeyTyped(evt);
            }
        });

        txtIDProduct.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtIDProduct.setMargin(new java.awt.Insets(3, 3, 3, 3));
        txtIDProduct.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtIDProductMouseClicked(evt);
            }
        });
        txtIDProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIDProductActionPerformed(evt);
            }
        });
        txtIDProduct.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtIDProductKeyTyped(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel2.setText("ID Product");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel3.setText("Product name");

        txtProductName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel7.setText("Type");

        cboType.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        cboType.setFocusable(false);
        cboType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboTypeItemStateChanged(evt);
            }
        });

        txtAmount.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtAmount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAmountActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel6.setText("Amount");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel9.setText("Price");

        btnNew.setBackground(new java.awt.Color(0, 0, 0));
        btnNew.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnNew.setText("New");
        btnNew.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, java.awt.Color.white, null, java.awt.Color.white));
        btnNew.setFocusable(false);
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });

        btnSave.setBackground(new java.awt.Color(0, 0, 0));
        btnSave.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSave.setText("Save");
        btnSave.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, java.awt.Color.white, null, java.awt.Color.white));
        btnSave.setFocusable(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new java.awt.Color(0, 0, 0));
        btnDelete.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, java.awt.Color.white, null, java.awt.Color.white));
        btnDelete.setFocusable(false);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnExit.setBackground(new java.awt.Color(0, 0, 0));
        btnExit.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnExit.setText("Exit");
        btnExit.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, java.awt.Color.white, null, java.awt.Color.white));
        btnExit.setFocusable(false);
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        jPanel4.setBackground(new java.awt.Color(255, 204, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "User"));

        lblUser.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblUser.setText("Phùng Quốc Vinh");

        lblPosition.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblPosition.setText("Mananger");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel15.setText("Position:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblUser)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblPosition)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(lblUser)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(lblPosition))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cboBrand.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        cboBrand.setFocusable(false);
        cboBrand.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboBrandItemStateChanged(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel13.setText("Brand");

        txtPrice.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnNew, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7)
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel6)
                            .addComponent(jLabel9)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboBrand, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtAmount)
                            .addComponent(txtProductName)
                            .addComponent(txtIDProduct)
                            .addComponent(cboType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtPrice))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIDProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtProductName)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cboType)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cboBrand)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtAmount)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(txtPrice))
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNew, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        cardStorage.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 260, 530));

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/storage/icon/SideLeft.png"))); // NOI18N
        jLabel12.setText("jLabel12");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 140, 150, 230));

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/storage/icon/TopLeft.png"))); // NOI18N
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 70, 100, 110));

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/storage/icon/Logotiny.png"))); // NOI18N
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 90, -1, 60));

        jLabel1.setFont(new java.awt.Font("VNI 24 Love", 0, 70)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 51, 102));
        jLabel1.setText(" Moon Flower");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 20, 340, 90));

        lblToday.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblToday.setText("15-11-2003");
        jPanel1.add(lblToday, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 40, 80, 20));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/storage/icon/MoonFlower.png"))); // NOI18N
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 130, -1));

        lblClock.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblClock.setText("10:22:30 AM");
        jPanel1.add(lblClock, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 20, 80, -1));

        tblListData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID PRODUCT", "PRODUCT NAME", "TYPE", "BRAND", "PRICE", "AMOUNT", "TOTAL MONEY", "DATE ADDED"
            }
        ));
        tblListData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblListDataMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblListData);
        if (tblListData.getColumnModel().getColumnCount() > 0) {
            tblListData.getColumnModel().getColumn(2).setMinWidth(60);
            tblListData.getColumnModel().getColumn(2).setMaxWidth(60);
            tblListData.getColumnModel().getColumn(3).setMinWidth(60);
            tblListData.getColumnModel().getColumn(3).setMaxWidth(60);
            tblListData.getColumnModel().getColumn(5).setMinWidth(60);
            tblListData.getColumnModel().getColumn(5).setMaxWidth(60);
        }

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 360, 630, 140));

        lblImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/storage/icon/SideRight.png"))); // NOI18N
        lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImageMouseClicked(evt);
            }
        });
        jPanel1.add(lblImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 200, 110, 110));

        lblRecord.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        lblRecord.setText("0 of 0");
        jPanel1.add(lblRecord, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 330, 40, 30));

        btnExport.setBackground(new java.awt.Color(0, 204, 153));
        btnExport.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnExport.setText("Export excel file");
        btnExport.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnExport.setFocusable(false);
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });
        jPanel1.add(btnExport, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 280, 100, 35));

        cboSort.setBackground(new java.awt.Color(51, 51, 51));
        cboSort.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ID Product", "Name", "Type", "Brand", "Price" }));
        cboSort.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "Sort by"));
        cboSort.setLightWeightPopupEnabled(false);
        cboSort.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboSortItemStateChanged(evt);
            }
        });
        cboSort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSortActionPerformed(evt);
            }
        });
        jPanel1.add(cboSort, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 230, 100, -1));

        btnLast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/storage/icon/Down.png"))); // NOI18N
        btnLast.setToolTipText("");
        btnLast.setContentAreaFilled(false);
        btnLast.setFocusable(false);
        btnLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLastActionPerformed(evt);
            }
        });
        jPanel1.add(btnLast, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 480, 30, 20));

        btnNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/storage/icon/Next.png"))); // NOI18N
        btnNext.setToolTipText("");
        btnNext.setContentAreaFilled(false);
        btnNext.setFocusable(false);
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });
        jPanel1.add(btnNext, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 440, 30, 20));

        btnFirst.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/storage/icon/Up.png"))); // NOI18N
        btnFirst.setToolTipText("");
        btnFirst.setContentAreaFilled(false);
        btnFirst.setFocusable(false);
        btnFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFirstActionPerformed(evt);
            }
        });
        jPanel1.add(btnFirst, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 360, 30, 20));

        btnPrev.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/storage/icon/Prev.png"))); // NOI18N
        btnPrev.setToolTipText("");
        btnPrev.setContentAreaFilled(false);
        btnPrev.setFocusable(false);
        btnPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrevActionPerformed(evt);
            }
        });
        jPanel1.add(btnPrev, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 400, 30, 20));

        cardStorage.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 0, 690, 530));

        jPanel5.setBackground(new java.awt.Color(0, 51, 51));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblHome.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblHome.setForeground(new java.awt.Color(255, 255, 255));
        lblHome.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHome.setText("HOME");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblHome, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblHome, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
        );

        cardStorage.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 530, 330, 40));

        jPanel6.setBackground(new java.awt.Color(0, 51, 51));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblAccount.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblAccount.setForeground(new java.awt.Color(255, 255, 255));
        lblAccount.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAccount.setText("ACCOUNT");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblAccount, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblAccount, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
        );

        cardStorage.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 530, 310, 40));

        cardContainer.add(cardStorage, "card2");

        getContentPane().add(cardContainer, "card2");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void txtIDProductMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtIDProductMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIDProductMouseClicked

    private void txtIDProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIDProductActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIDProductActionPerformed

    private void txtIDProductKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtIDProductKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIDProductKeyTyped

    private void cboTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboTypeItemStateChanged
        String k = (String.valueOf(cboType.getSelectedItem())).toUpperCase();
        //txtCodeTypeProduct.setText(k.substring(0, 2));
    }//GEN-LAST:event_cboTypeItemStateChanged

    private void txtAmountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAmountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAmountActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        clearForm();
        lblRecord.setText(setRecord());;
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (check())
            save();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        delete();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed

    }//GEN-LAST:event_btnExitActionPerformed

    private void tblListDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblListDataMouseClicked
        row = tblListData.getSelectedRow();
        edit();
        btnDelete.setEnabled(true);
    }//GEN-LAST:event_tblListDataMouseClicked

    private void lblImageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseClicked
        chooseImage();
    }//GEN-LAST:event_lblImageMouseClicked

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        ExportFileExcel();
    }//GEN-LAST:event_btnExportActionPerformed

    private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastActionPerformed
        last();
    }//GEN-LAST:event_btnLastActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        next();
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstActionPerformed
        first();
    }//GEN-LAST:event_btnFirstActionPerformed

    private void btnPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevActionPerformed
        prev();
    }//GEN-LAST:event_btnPrevActionPerformed

    private void cboBrandItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboBrandItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cboBrandItemStateChanged

    private void txtSearchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyTyped

    }//GEN-LAST:event_txtSearchKeyTyped

    private void txtSearchCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtSearchCaretUpdate
        search();
    }//GEN-LAST:event_txtSearchCaretUpdate

    private void cboSortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSortActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboSortActionPerformed

    private void cboSortItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboSortItemStateChanged
        Sort();
    }//GEN-LAST:event_cboSortItemStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(StorageImport.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StorageImport.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StorageImport.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StorageImport.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StorageImport().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnFirst;
    private javax.swing.JButton btnLast;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrev;
    private javax.swing.JButton btnSave;
    private javax.swing.JPanel cardContainer;
    private javax.swing.JPanel cardStorage;
    private javax.swing.JComboBox<String> cboBrand;
    private javax.swing.JComboBox<String> cboSort;
    private javax.swing.JComboBox<String> cboType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblAccount;
    private javax.swing.JLabel lblClock;
    private javax.swing.JLabel lblHome;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblPosition;
    private javax.swing.JLabel lblRecord;
    private javax.swing.JLabel lblToday;
    private javax.swing.JLabel lblUser;
    private javax.swing.JTable tblListData;
    private javax.swing.JTextField txtAmount;
    private javax.swing.JTextField txtIDProduct;
    private javax.swing.JFormattedTextField txtPrice;
    private javax.swing.JTextField txtProductName;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
