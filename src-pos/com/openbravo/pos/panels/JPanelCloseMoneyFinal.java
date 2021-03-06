//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2007-2009 Openbravo, S.L.
//    http://www.openbravo.com/product/pos
//
//    This file is part of Openbravo POS.
//
//    Openbravo POS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Openbravo POS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Openbravo POS.  If not, see <http://www.gnu.org/licenses/>.
package com.openbravo.pos.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.gui.TableRendererBasic;
import com.openbravo.data.loader.Datas;
import com.openbravo.data.loader.SerializerWriteBasic;
import com.openbravo.data.loader.StaticSentence;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.BeanFactoryApp;
import com.openbravo.pos.forms.BeanFactoryException;
import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.forms.JPanelView;
import com.openbravo.pos.inventory.TaxCategoryInfo;
import com.openbravo.pos.printer.TicketParser;
import com.openbravo.pos.printer.TicketPrinterException;
import com.openbravo.pos.scripting.ScriptEngine;
import com.openbravo.pos.scripting.ScriptException;
import com.openbravo.pos.scripting.ScriptFactory;

/**
 *
 * @author adrianromero
 */
public class JPanelCloseMoneyFinal extends JPanel implements JPanelView, BeanFactoryApp {

    private AppView m_App;
    private DataLogicSystem m_dlSystem;
    private Double amountCash;
    private Double amountCashDollar;
    private Double amountCheque;
    private Double amountCreditCard;
    private Double amountDebitCard;
    //Variables con los valores ingresados por el usuario en la pantalla anterior
    private Double insertedCash;
    private Double insertedCashDollar;
    private Double insertedCheque;
    private Double insertedCreditCard;
    private Double insertedDebitCard;

    private PaymentsModel m_PaymentsToClose = null;

    private TicketParser m_TTP;
    //private Double sumAllPay;

    private String m_UserID;
    private TaxCategoryInfo m_People;

    JPanelCloseMoney closeMoney;
    private Double totalAmtCash;
    private Double totalAmtDollarCash;
    private Double differenceCash;
    private Double differenceDollar;
    private String sDifferenceDollar;

    /**
     * Creates new form JPanelCloseMoney
     */
    public JPanelCloseMoneyFinal() {

        initComponents();
    }

    @Override
    public void init(AppView app) throws BeanFactoryException {
        m_App = app;
        m_dlSystem = (DataLogicSystem) m_App.getBean(DataLogicSystem.class.getName());
        m_TTP = new TicketParser(m_App.getDeviceTicket(), m_dlSystem);

        m_jTicketTable.setDefaultRenderer(Object.class, new TableRendererBasic(
                new Formats[]{new FormatsPayment(), Formats.CURRENCY, Formats.DOLLAR_CURRENCY}));
        m_jTicketTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        m_jScrollTableTicket.getVerticalScrollBar().setPreferredSize(new Dimension(25, 25));
        m_jTicketTable.getTableHeader().setReorderingAllowed(false);
        m_jTicketTable.setRowHeight(25);
        m_jTicketTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        m_jsalestable.setDefaultRenderer(Object.class, new TableRendererBasic(
                new Formats[]{Formats.STRING, Formats.CURRENCY, Formats.CURRENCY, Formats.CURRENCY}));
        m_jsalestable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        m_jScrollSales.getVerticalScrollBar().setPreferredSize(new Dimension(25, 25));
        m_jsalestable.getTableHeader().setReorderingAllowed(false);
        m_jsalestable.setRowHeight(25);
        m_jsalestable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        amountCashDollar = 0.0;
        amountCash = 0.0;
        amountDebitCard = 0.0;
        amountCreditCard = 0.0;
        amountCheque = 0.0;
        insertedCash = 0.0;
        insertedCashDollar = 0.0;
        insertedCheque = 0.0;
        insertedCreditCard = 0.0;
        insertedDebitCard = 0.0;
    }

    @Override
    public Object getBean() {
        return this;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.CloseTPV");
    }

    @Override
    public void activate() throws BasicException {
        loadData();
        closeCash();
    }

    @Override
    public boolean deactivate() {
        // I must be allowed to cancel the deactivate   
        return true;
    }

    private void loadData() throws BasicException {
        closeMoney = (JPanelCloseMoney) m_App.getBean(JPanelCloseMoney.class.getName());
        insertedCash = closeMoney.getTotalCash();
        insertedCheque = closeMoney.getTotalCheque();
        insertedCreditCard = closeMoney.getTotalCreditCards();
        insertedDebitCard = closeMoney.getTotalDebitCards();
        totalAmtCash = closeMoney.getTotalPay();
        totalAmtDollarCash = closeMoney.getTotalPayDollar();
        insertedCashDollar = closeMoney.getTotalCashDollar();
        m_UserID = closeMoney.getUserID();
        m_People = closeMoney.getPeople();
        
        // Reset
        m_jSequence.setText(null);
        m_jMinDate.setText(null);
        m_jMaxDate.setText(null);
        m_jPrintCash.setEnabled(false);
        m_jCount.setText(null); // AppLocal.getIntString("label.noticketstoclose");
        m_jCash.setText(null);
        //  Dixon Martinez
        m_jCashDollar.setText(null);
        m_jDifferenceDollar.setText(null);
        m_jRegisterDollar.setText(null);

        m_jSales.setText(null);
        m_jSalesSubtotal.setText(null);
        m_jSalesTaxes.setText(null);
        m_jSalesTotal.setText(null);

        m_jTicketTable.setModel(new DefaultTableModel());
        m_jsalestable.setModel(new DefaultTableModel());

        // LoadData
        m_PaymentsToClose = PaymentsModel.loadInstance(m_App, m_UserID);
        m_PaymentsToClose.setUserName(m_People);
        asignarMontos(m_PaymentsToClose);

        // Populate Data
        m_jSequence.setText(m_PaymentsToClose.printSequence());
        m_jMinDate.setText(m_PaymentsToClose.printDateStart());
        m_jMaxDate.setText(m_PaymentsToClose.printDateEnd());

        if ((m_PaymentsToClose.getPayments() != 0 || m_PaymentsToClose.getDollarPayments() != 0)
                || (m_PaymentsToClose.getSales() != 0 || m_PaymentsToClose.getSalesDollar() != 0)) {

            m_jPrintCash.setEnabled(true);

            m_jCount.setText(m_PaymentsToClose.printPayments());
            m_jCountDollar.setText(m_PaymentsToClose.printDollarPayments());
            m_jRegister.setText(Formats.CURRENCY.formatValue(totalAmtCash));
            m_jRegisterDollar.setText(Formats.DOLLAR_CURRENCY.formatValue(totalAmtDollarCash));
            m_jCash.setText(m_PaymentsToClose.printPaymentsTotal());
            m_jCashDollar.setText(Formats.DOLLAR_CURRENCY.formatValue(m_PaymentsToClose.getPaymentsDollarTotal()));
            differenceCash = totalAmtCash - m_PaymentsToClose.getPaymentsTotal(); // .subtract(BigDecimal.valueOf());
            m_jDifference.setText(Formats.CURRENCY.formatValue(differenceCash));
            if (differenceCash != 0.0) {
                m_jDifference.setForeground(Color.red);
            }

            sDifferenceDollar = Formats.DOLLAR_CURRENCY.formatValue(totalAmtDollarCash - m_PaymentsToClose.getPaymentsDollarTotal());
            differenceDollar = (Double) Formats.DOLLAR_CURRENCY.parseValue(sDifferenceDollar);
            m_jDifferenceDollar.setText(Formats.DOLLAR_CURRENCY.formatValue(differenceDollar));
            if (differenceDollar != 0.0) {
                m_jDifferenceDollar.setForeground(Color.red);
            }

            m_jSales.setText(m_PaymentsToClose.printSales());
            m_jSalesSubtotal.setText(m_PaymentsToClose.printSalesBase());
            m_jSalesTaxes.setText(m_PaymentsToClose.printSalesTaxes());
            m_jSalesTotal.setText(m_PaymentsToClose.printSalesTotal());

            m_jSalesDollar.setText(m_PaymentsToClose.printSalesDollar());
            m_jSalesSubtotalDollar.setText(m_PaymentsToClose.printSalesBaseDollar());
            m_jSalesTaxesDollar.setText(m_PaymentsToClose.printSalesDollarTaxes());
            m_jSalesTotalDollar.setText(m_PaymentsToClose.printSalesDollarTotal());
        }

        m_jTicketTable.setModel(m_PaymentsToClose.getPaymentsModel());

        TableColumnModel jColumns = m_jTicketTable.getColumnModel();
        jColumns.getColumn(0).setPreferredWidth(200);
        jColumns.getColumn(0).setResizable(false);
        jColumns.getColumn(1).setPreferredWidth(100);
        jColumns.getColumn(1).setResizable(false);

        m_jsalestable.setModel(m_PaymentsToClose.getSalesModel());

        jColumns = m_jsalestable.getColumnModel();
        jColumns.getColumn(0).setPreferredWidth(200);
        jColumns.getColumn(0).setResizable(false);
        jColumns.getColumn(1).setPreferredWidth(100);
        jColumns.getColumn(1).setResizable(false);

        compararValores();
    }

    private void printPayments(String report) {

        String sresource = m_dlSystem.getResourceAsXML(report);
        if (sresource == null) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"));
            msg.show(this);
        } else {
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                script.put("payments", m_PaymentsToClose);
                m_TTP.printTicket(script.eval(sresource).toString());
            } catch (ScriptException | TicketPrinterException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
                msg.show(this);
            }
        }
    }

    private void asignarMontos(PaymentsModel m_PaymentsToClose) {
        PaymentsModel.PaymentsLine payLine;
        for (int i = 0; i < m_PaymentsToClose.getPaymentLines().size(); i++) {
            payLine = m_PaymentsToClose.getPaymentLines().get(i);
            if (payLine.getType().toUpperCase().equals("CASH")) {
                amountCash = payLine.getValue();
            }
            if(payLine.getType().toUpperCase().equals("CREDITCARD")) {
                amountCreditCard = payLine.getValue();
            }
            if(payLine.getType().toUpperCase().equals("DEBITCARD")) {
                amountDebitCard = payLine.getValue();
            }
            if (payLine.getType().toUpperCase().contains("CHEQUE")) {
                amountCheque = payLine.getValue();
            }
            if (payLine.getType().toUpperCase().equals("CASH_DOLLAR")) {
                amountCashDollar = payLine.getValue();
            }
        }
    }

    private void compararValores() {
        if (amountDebitCard == null) {
            amountDebitCard = 0.0;
        }
        if (amountCreditCard == null) {
            amountCreditCard = 0.0;
        }
        
        if (amountCash == null) {
            amountCash = 0.0;
        }
        if (amountCashDollar == null) {
            amountCashDollar = 0.0;
        }
        if (amountCheque == null) {
            amountCheque = 0.0;
        }

        DefaultTableModel modeltbl = new ModelDifference();
        modeltbl.addColumn(AppLocal.getIntString("Label.PaymentType"));
        modeltbl.addColumn(AppLocal.getIntString("Label.MoneyInBox"));
        modeltbl.addColumn(AppLocal.getIntString("Label.MoneyRegistered"));
        modeltbl.addColumn(AppLocal.getIntString("label.difference"));

        if (insertedDebitCard.compareTo(amountDebitCard) != 0) {
            Vector vectorTbl = new Vector();
            vectorTbl.add(AppLocal.getIntString("tab.magdebitcard"));
            vectorTbl.add(Formats.CURRENCY.formatValue(amountDebitCard));
            vectorTbl.add(Formats.CURRENCY.formatValue(insertedDebitCard));
            vectorTbl.add(Formats.CURRENCY.formatValue(insertedDebitCard - amountDebitCard));
            modeltbl.addRow(vectorTbl);
        }
        
        if (insertedCreditCard.compareTo(amountCreditCard) != 0) {
            Vector vectorTbl = new Vector();
            vectorTbl.add(AppLocal.getIntString("tab.magcreditcard"));
            vectorTbl.add(Formats.CURRENCY.formatValue(amountCreditCard));
            vectorTbl.add(Formats.CURRENCY.formatValue(insertedCreditCard));
            vectorTbl.add(Formats.CURRENCY.formatValue(insertedCreditCard - amountCreditCard));
            modeltbl.addRow(vectorTbl);
        }
        
        if (insertedCash.compareTo(amountCash) != 0) {
            Vector vectorTbl = new Vector();
            vectorTbl.add(AppLocal.getIntString("tab.cash"));
            vectorTbl.add(Formats.CURRENCY.formatValue(amountCash));
            vectorTbl.add(Formats.CURRENCY.formatValue(insertedCash));
            vectorTbl.add(Formats.CURRENCY.formatValue(insertedCash - amountCash));
            modeltbl.addRow(vectorTbl);
        }

        if (insertedCashDollar.compareTo(amountCashDollar) != 0) {
            Vector vectorTbl = new Vector();
            vectorTbl.add(AppLocal.getIntString("tab.dollar"));
            //vectorTbl.add(CurrencyChange.changePesoToDollar(amountCashDollar));
            vectorTbl.add(amountCashDollar);
            vectorTbl.add(Formats.DOLLAR_CURRENCY.formatValue(insertedCashDollar));
            vectorTbl.add(Formats.DOLLAR_CURRENCY.formatValue(insertedCashDollar - amountCashDollar));
            modeltbl.addRow(vectorTbl);
        }

        if (insertedCheque.compareTo(amountCheque) != 0) {
            Vector vectorTbl = new Vector();
            vectorTbl.add(AppLocal.getIntString("tab.cheque"));
            vectorTbl.add(Formats.CURRENCY.formatValue(amountCheque));
            vectorTbl.add(Formats.CURRENCY.formatValue(insertedCheque));
            vectorTbl.add(Formats.CURRENCY.formatValue(insertedCheque - amountCheque));
            modeltbl.addRow(vectorTbl);
        }

    }

    public class ModelDifference extends DefaultTableModel {

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    private class FormatsPayment extends Formats {

        @Override
        protected String formatValueInt(Object value) {
            return AppLocal.getIntString("transpayment." + (String) value);
        }

        @Override
        protected Object parseValueInt(String value) throws ParseException {
            return value;
        }

        @Override
        public int getAlignment() {
            return javax.swing.SwingConstants.LEFT;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        m_jSequence = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        m_jMinDate = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        m_jMaxDate = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        m_jScrollTableTicket = new javax.swing.JScrollPane();
        m_jTicketTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        m_jCount = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        m_jCash = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        m_jDifference = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        m_jRegister = new javax.swing.JTextField();
        m_jDifferenceDollar = new javax.swing.JTextField();
        m_jRegisterDollar = new javax.swing.JTextField();
        m_jCashDollar = new javax.swing.JTextField();
        m_jCountDollar = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        m_jSalesTotal = new javax.swing.JTextField();
        m_jScrollSales = new javax.swing.JScrollPane();
        m_jsalestable = new javax.swing.JTable();
        m_jSalesTaxes = new javax.swing.JTextField();
        m_jSalesSubtotal = new javax.swing.JTextField();
        m_jSales = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        m_jSalesTotalDollar = new javax.swing.JTextField();
        m_jSalesDollar = new javax.swing.JTextField();
        m_jSalesSubtotalDollar = new javax.swing.JTextField();
        m_jSalesTaxesDollar = new javax.swing.JTextField();
        m_jPrintCash = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(AppLocal.getIntString("label.datestitle"))); // NOI18N

        jLabel11.setText(AppLocal.getIntString("label.sequence")); // NOI18N

        m_jSequence.setEditable(false);
        m_jSequence.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel2.setText(AppLocal.getIntString("Label.StartDate")); // NOI18N

        m_jMinDate.setEditable(false);
        m_jMinDate.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel3.setText(AppLocal.getIntString("Label.EndDate")); // NOI18N

        m_jMaxDate.setEditable(false);
        m_jMaxDate.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jSequence, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jMinDate, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jMaxDate, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(m_jSequence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(m_jMinDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(m_jMaxDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(AppLocal.getIntString("label.paymentstitle"))); // NOI18N

        m_jScrollTableTicket.setMinimumSize(new java.awt.Dimension(350, 140));
        m_jScrollTableTicket.setPreferredSize(new java.awt.Dimension(350, 140));

        m_jTicketTable.setFocusable(false);
        m_jTicketTable.setIntercellSpacing(new java.awt.Dimension(0, 1));
        m_jTicketTable.setRequestFocusEnabled(false);
        m_jTicketTable.setShowVerticalLines(false);
        m_jScrollTableTicket.setViewportView(m_jTicketTable);

        jLabel1.setText(AppLocal.getIntString("Label.Tickets")); // NOI18N

        m_jCount.setEditable(false);
        m_jCount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel4.setText(AppLocal.getIntString("Label.Cash")); // NOI18N

        m_jCash.setEditable(false);
        m_jCash.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jLabel8.setText(bundle.getString("label.difference")); // NOI18N

        m_jDifference.setEditable(false);
        m_jDifference.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel9.setText(bundle.getString("label.registered")); // NOI18N

        m_jRegister.setEditable(false);
        m_jRegister.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        m_jDifferenceDollar.setEditable(false);
        m_jDifferenceDollar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        m_jRegisterDollar.setEditable(false);
        m_jRegisterDollar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        m_jCashDollar.setEditable(false);
        m_jCashDollar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        m_jCountDollar.setEditable(false);
        m_jCountDollar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(m_jScrollTableTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jCount, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(m_jCountDollar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(m_jCash, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(m_jDifference, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(m_jRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jDifferenceDollar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jCashDollar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jRegisterDollar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(m_jScrollTableTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(m_jCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jCountDollar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(m_jCash, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(m_jRegister, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(m_jDifference, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jDifferenceDollar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(38, 38, 38))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(m_jCashDollar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jRegisterDollar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(AppLocal.getIntString("label.salestitle"))); // NOI18N

        m_jSalesTotal.setEditable(false);
        m_jSalesTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        m_jsalestable.setFocusable(false);
        m_jsalestable.setIntercellSpacing(new java.awt.Dimension(0, 1));
        m_jsalestable.setRequestFocusEnabled(false);
        m_jsalestable.setShowVerticalLines(false);
        m_jScrollSales.setViewportView(m_jsalestable);

        m_jSalesTaxes.setEditable(false);
        m_jSalesTaxes.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        m_jSalesSubtotal.setEditable(false);
        m_jSalesSubtotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        m_jSales.setEditable(false);
        m_jSales.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel5.setText(AppLocal.getIntString("label.sales")); // NOI18N

        jLabel6.setText(AppLocal.getIntString("label.subtotalcash")); // NOI18N

        jLabel12.setText(AppLocal.getIntString("label.taxcash")); // NOI18N

        jLabel7.setText(AppLocal.getIntString("label.totalcash")); // NOI18N

        m_jSalesTotalDollar.setEditable(false);
        m_jSalesTotalDollar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        m_jSalesDollar.setEditable(false);
        m_jSalesDollar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        m_jSalesSubtotalDollar.setEditable(false);
        m_jSalesSubtotalDollar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        m_jSalesTaxesDollar.setEditable(false);
        m_jSalesTaxesDollar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(m_jScrollSales, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jSales, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jSalesSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jSalesTaxes, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jSalesTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jSalesDollar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jSalesSubtotalDollar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jSalesTaxesDollar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jSalesTotalDollar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jScrollSales, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(m_jSalesDollar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(m_jSalesSubtotalDollar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(m_jSalesTaxesDollar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(m_jSalesTotalDollar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel5)
                                .addComponent(m_jSales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel6)
                                .addComponent(m_jSalesSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel12)
                                .addComponent(m_jSalesTaxes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel7)
                                .addComponent(m_jSalesTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        m_jPrintCash.setText(AppLocal.getIntString("Button.PrintCash")); // NOI18N
        m_jPrintCash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jPrintCashActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(m_jPrintCash)
                        .addGap(86, 86, 86))
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addComponent(m_jPrintCash)
                .addContainerGap())
        );

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    public void closeCash() {
        Date dNow = new Date();
        String userID = null;
        try {
            // Close the case if pending closing.
            Object[] valcash = m_dlSystem.findActiveCash(m_UserID);
            userID = valcash[4].toString();
            if (m_App.getActiveCashDateEnd() == null) {
                new StaticSentence(m_App.getSession(),
                         "UPDATE CLOSEDCASH SET DATEEND = ?, DIFFERENCECASH = ?, DIFFERENCEDOLLAR = ? WHERE PERSON = ? AND MONEY = ?",
                         new SerializerWriteBasic(new Datas[]{Datas.TIMESTAMP, Datas.DOUBLE, Datas.DOUBLE, Datas.STRING, Datas.STRING}))
                        .exec(new Object[]{dNow, differenceCash, differenceDollar, userID, valcash[0].toString()});
                saveDetails(valcash[0].toString(), "cash", insertedCash);
                saveDetails(valcash[0].toString(), "cash_dollar", insertedCashDollar);
                saveDetails(valcash[0].toString(), "cheque", insertedCheque);
                saveDetails(valcash[0].toString(), "debitcard", insertedCreditCard);
                saveDetails(valcash[0].toString(), "creditcard", insertedDebitCard);
            }
        } catch (BasicException e) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotclosecash"), e);
            msg.show(this);
        }

        try {
            // Create a new box       
            m_App.setActiveCash(UUID.randomUUID().toString(), m_App.getActiveCashSequence() + 1, dNow, null);

            // create the active transmission
            if (m_UserID == null ? userID == null : m_UserID.equals(userID)) {
                m_dlSystem.execInsertCash(
                        new Object[]{m_App.getActiveCashIndex(),
                            m_App.getActiveCashSequence(),
                            m_App.getActiveCashDateStart(),
                            m_App.getActiveCashDateEnd(),
                            m_UserID});
            }

            // we end date
            m_PaymentsToClose.setDateEnd(dNow);
            m_PaymentsToClose.setDifferenceCash(differenceCash);
            m_PaymentsToClose.setDifferenceDollar(differenceDollar);
            
            m_PaymentsToClose.setRegisteredCash(totalAmtCash);
            m_PaymentsToClose.setRegisteredDollar(totalAmtDollarCash);
            m_PaymentsToClose.setPrintDollarAmt(totalAmtDollarCash != null && totalAmtDollarCash > 0.0);
            

            m_jMaxDate.setText(Formats.TIMESTAMP.formatValue(dNow)/*Formats.DATE.formatValue(m_PaymentsToClose.getDateEnd())*/);

            // print report
            printPayments("Printer.CloseCash");
            if(closeMoney.getPrintSalesReport()) {
                printPayments("Printer.CloseCashDetailSales");
            }

            // We show the message
            JOptionPane.showMessageDialog(this, AppLocal.getIntString("message.closecashok"), AppLocal.getIntString("message.title"), JOptionPane.INFORMATION_MESSAGE);
        } catch (BasicException e) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotclosecash"), e);
            msg.show(this);
        }
    }

    public void saveDetails(String money, String type, Double amount) {
        try {
            new StaticSentence(m_App.getSession(),
                     "INSERT INTO CLOSEDCASHLINES(id, money, payment, total, pointofsales, cardtype) "
                    + "VALUES (?, ?, ?, ?, ?, ?)",
                     new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.STRING, Datas.STRING}))
                    .exec(new Object[]{UUID.randomUUID().toString(), money, type, amount, "", ""});
        } catch (BasicException e) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotclosecash"), e);
            msg.show(this);
        }

    }


private void m_jPrintCashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jPrintCashActionPerformed
    // print report
    printPayments("Printer.CloseCash");
    if(closeMoney.getPrintSalesReport()) {
          printPayments("Printer.CloseCashDetailSales");
      }

}//GEN-LAST:event_m_jPrintCashActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JTextField m_jCash;
    private javax.swing.JTextField m_jCashDollar;
    private javax.swing.JTextField m_jCount;
    private javax.swing.JTextField m_jCountDollar;
    private javax.swing.JTextField m_jDifference;
    private javax.swing.JTextField m_jDifferenceDollar;
    private javax.swing.JTextField m_jMaxDate;
    private javax.swing.JTextField m_jMinDate;
    private javax.swing.JButton m_jPrintCash;
    private javax.swing.JTextField m_jRegister;
    private javax.swing.JTextField m_jRegisterDollar;
    private javax.swing.JTextField m_jSales;
    private javax.swing.JTextField m_jSalesDollar;
    private javax.swing.JTextField m_jSalesSubtotal;
    private javax.swing.JTextField m_jSalesSubtotalDollar;
    private javax.swing.JTextField m_jSalesTaxes;
    private javax.swing.JTextField m_jSalesTaxesDollar;
    private javax.swing.JTextField m_jSalesTotal;
    private javax.swing.JTextField m_jSalesTotalDollar;
    private javax.swing.JScrollPane m_jScrollSales;
    private javax.swing.JScrollPane m_jScrollTableTicket;
    private javax.swing.JTextField m_jSequence;
    private javax.swing.JTable m_jTicketTable;
    private javax.swing.JTable m_jsalestable;
    // End of variables declaration//GEN-END:variables

}
