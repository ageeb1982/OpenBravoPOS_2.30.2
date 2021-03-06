/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.sales.currency;

import java.awt.Component;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.openbravo.basic.BasicException;
import com.openbravo.beans.JCalendarDialog;
import com.openbravo.data.gui.ComboBoxValModel;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.data.user.EditorRecord;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;

/**
 *
 * @author tt-01
 */
public final class ConversionRateView extends javax.swing.JPanel implements EditorRecord{

    /**
	 * 
	 */
	private static final long serialVersionUID = -724481632552377893L;

	private Object m_ConversionRateID;
    
    private ComboBoxValModel currenBoxValMod;
    private ComboBoxValModel currenToBoxValMod;
    private final DataLogicConversionRate dlConversionRate;
    
    /**
     * Creates new form ConversionRateView
     * @param m_App
     * @param m_Dirty 
     */
    public ConversionRateView(AppView m_App, DirtyManager m_Dirty) {
        initComponents();
        
        dlConversionRate = (DataLogicConversionRate) m_App.getBean(DataLogicConversionRate.class.getName());
        
        currenBoxValMod = new ComboBoxValModel();
        currenToBoxValMod = new ComboBoxValModel();
        jTxtDivRate.getDocument().addDocumentListener(m_Dirty);
        jTxtMultRate.getDocument().addDocumentListener(m_Dirty);
        
        txtValidFrom.getDocument().addDocumentListener(m_Dirty);
        txtValidTo.getDocument().addDocumentListener(m_Dirty);
        jCBIsActive.addActionListener(m_Dirty);
        
        jTxtDivRate.getDocument().addDocumentListener(new DivRateManager());
        jTxtMultRate.getDocument().addDocumentListener(new MultRateManager());
        
        
        writeValueEOF();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jCmbCurrency = new javax.swing.JComboBox<>();
        jCmbCurrencyTo = new javax.swing.JComboBox<>();
        jCBIsActive = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        txtValidFrom = new javax.swing.JTextField();
        btnValidFrom = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        txtValidTo = new javax.swing.JTextField();
        btnValidTo = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jTxtDivRate = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTxtMultRate = new javax.swing.JTextField();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jLabel1.setText(bundle.getString("Label.Currency")); // NOI18N

        jLabel2.setText(bundle.getString("Label.CurrencyTo")); // NOI18N

        jCBIsActive.setText(bundle.getString("label.IsActive")); // NOI18N

        jLabel7.setText(AppLocal.getIntString("Label.ValidFrom")); // NOI18N

        btnValidFrom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/date.png"))); // NOI18N
        btnValidFrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnValidFromActionPerformed(evt);
            }
        });

        jLabel8.setText(AppLocal.getIntString("Label.ValidTo")); // NOI18N

        btnValidTo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/date.png"))); // NOI18N
        btnValidTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnValidToActionPerformed(evt);
            }
        });

        jLabel4.setText(bundle.getString("Label.DivideRate")); // NOI18N

        jLabel5.setText(bundle.getString("Label.MultiplyRate")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCBIsActive)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(jCmbCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtValidFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnValidFrom)
                                .addGap(27, 27, 27)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jCmbCurrencyTo, 0, 141, Short.MAX_VALUE)
                            .addComponent(jTxtMultRate)
                            .addComponent(txtValidTo))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnValidTo))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addComponent(jTxtDivRate, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jCmbCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCmbCurrencyTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel7)
                                    .addComponent(txtValidFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(12, 12, 12))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8)
                                    .addComponent(txtValidTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnValidFrom)
                            .addComponent(btnValidTo))
                        .addGap(12, 12, 12)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTxtDivRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jTxtMultRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCBIsActive)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnValidFromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnValidFromActionPerformed
        Date date;
        try {
            date = (Date) Formats.TIMESTAMP.parseValue(txtValidFrom.getText());
        } catch (BasicException e) {
            date = null;
        }
        date = JCalendarDialog.showCalendarTimeHours(this, date);
        if (date != null) {
            txtValidFrom.setText(Formats.TIMESTAMP.formatValue(date));
        }
    }//GEN-LAST:event_btnValidFromActionPerformed

    private void btnValidToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnValidToActionPerformed
         Date date;
        try {
            date = (Date) Formats.TIMESTAMP.parseValue(txtValidTo.getText());
        } catch (BasicException e) {
            date = null;
        }
        date = JCalendarDialog.showCalendarTimeHours(this, date);
        if (date != null) {
            txtValidTo.setText(Formats.TIMESTAMP.formatValue(date));
        }
    }//GEN-LAST:event_btnValidToActionPerformed

    public void rate(Double value, String field) {
    	Double rate1 = value;
    	Double rate2 = 0.0;
    	Double one = 1.0;
    	
    	if(rate1 != 0.0) { //	No divid by zero
            rate2 = one / rate1; 
    	}
        
        if(field.equals("MultRate")) {
            blockDivRate = true;
            jTxtDivRate.setText(Formats.DOUBLE.formatValue(rate2));
        } else {
            blockMultRate = true;
            jTxtMultRate.setText(Formats.DOUBLE.formatValue(rate2));
        }
    	    	
    }
    @Override
    public void writeValueEOF() {
        initDefaultComponents(null);
        enableComponents(false);
    }

    @Override
    public void writeValueInsert() {
        initDefaultComponents(null);
        enableComponents(true);
        
    }

    @Override
    public void writeValueEdit(Object value) {
    	blockDivRate = true;
   	 	blockMultRate = true;
        Object[] convRate = (Object[]) value;
        m_ConversionRateID  = convRate[0];
        currenBoxValMod.setSelectedKey(convRate[1]);
        currenToBoxValMod.setSelectedKey(convRate[2]);
        jTxtDivRate.setText(Formats.DOUBLE.formatValue(convRate[3]));
        jCBIsActive.setSelected(convRate[4].equals("Y"));
        jTxtMultRate.setText(Formats.DOUBLE.formatValue(convRate[5]));
        txtValidFrom.setText(Formats.TIMESTAMP.formatValue(convRate[6]));
        txtValidTo.setText(Formats.TIMESTAMP.formatValue(convRate[7]));
        enableComponents(true);
        blockDivRate = false;
   	 	blockMultRate = false;
       
    }

    @Override
    public void writeValueDelete(Object value) {
        Object[] convRate = (Object[]) value;
        m_ConversionRateID  = convRate[0];
        currenBoxValMod.setSelectedKey(convRate[1]);
        currenToBoxValMod.setSelectedKey(convRate[2]);
        jTxtDivRate.setText(Formats.DOUBLE.formatValue(convRate[3]));
        jCBIsActive.setSelected(convRate[4].equals("Y"));
        jTxtMultRate.setText(Formats.DOUBLE.formatValue(convRate[5]));
        txtValidFrom.setText(Formats.TIMESTAMP.formatValue(convRate[6]));
        txtValidTo.setText(Formats.TIMESTAMP.formatValue(convRate[7]));
        enableComponents(false);
    }

    @Override
    public void refresh() {
        
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public Object createValue() throws BasicException {
    	 blockDivRate = true;
    	 blockMultRate = true;
        Object[] conversionRate = new Object[8];
        conversionRate[0] = m_ConversionRateID == null ? UUID.randomUUID().toString() : m_ConversionRateID;
        conversionRate[1] = currenBoxValMod.getSelectedKey();
        conversionRate[2] = currenToBoxValMod.getSelectedKey();
        conversionRate[3] = Formats.DOUBLE.parseValue(jTxtDivRate.getText());
        conversionRate[4] = jCBIsActive.isSelected() ? "Y" : "N";
        conversionRate[5] = Formats.DOUBLE.parseValue(jTxtMultRate.getText());
        conversionRate[6] = Formats.TIMESTAMP.parseValue(txtValidFrom.getText());
        conversionRate[7] = Formats.TIMESTAMP.parseValue(txtValidTo.getText());
        blockDivRate = false;
        blockMultRate = false;
        return conversionRate;
    }
    
     private void initDefaultComponents(String value) {
         m_ConversionRateID  = value;
         currenBoxValMod.setSelectedKey(value);
         currenToBoxValMod.setSelectedKey(value);
         jTxtDivRate.setText(value);
         jCBIsActive.setSelected(true);
         jTxtMultRate.setText(value);
         txtValidFrom.setText(value);
         txtValidTo.setText(value);
    }
    
    private void enableComponents(boolean value) {
        jCmbCurrency.setEnabled(value);
        jCmbCurrencyTo.setEnabled(value);
        jTxtDivRate.setEnabled(value);
        jCBIsActive.setEnabled(value);
        jTxtMultRate.setEnabled(value);
        txtValidFrom.setEnabled(value);
        txtValidTo.setEnabled(value);
    }
    
    public void activate() throws BasicException {
        List a = CurrencyInfo.getCurrenciesAvailable();
        currenBoxValMod = new ComboBoxValModel(a);
        currenToBoxValMod = new ComboBoxValModel(a);
        jCmbCurrency.setModel(currenBoxValMod);
        jCmbCurrencyTo.setModel(currenToBoxValMod);
        
    }

    public static boolean blockMultRate = false;
    static boolean blockDivRate = false;
    private class DivRateManager implements DocumentListener {
    
    	@Override
        public void insertUpdate(DocumentEvent e) {
    		if(!blockDivRate) {
    			String rate = jTxtDivRate.getText().trim();
                if(rate != null
                        && rate.length() > 0) {
                    rate(readCurrency(rate), "DivRate");
                    blockMultRate = false;
                } else {
                    jTxtMultRate.setText("");
                }
    		} 
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }
        
    }

    private class MultRateManager implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
        	if(!blockMultRate) {
                    String rate = jTxtMultRate.getText().trim();
                    if(rate != null 
                            && rate.length() > 0) {
                        rate(readCurrency(rate), "MultRate");
                        blockDivRate = false;
                    } else {
                            jTxtDivRate.setText("");
                    }
        	} 
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }
        
    }

    private static Double readCurrency(String sValue) {
        try {
            return (Double) Formats.DOUBLE.parseValue(sValue);
        } catch (BasicException e) {
            return null;
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnValidFrom;
    private javax.swing.JButton btnValidTo;
    private javax.swing.JCheckBox jCBIsActive;
    private javax.swing.JComboBox<String> jCmbCurrency;
    private javax.swing.JComboBox<String> jCmbCurrencyTo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTxtDivRate;
    private javax.swing.JTextField jTxtMultRate;
    private javax.swing.JTextField txtValidFrom;
    private javax.swing.JTextField txtValidTo;
    // End of variables declaration//GEN-END:variables
}
