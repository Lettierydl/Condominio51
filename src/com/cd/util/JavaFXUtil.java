/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.util;

import com.cd.sis.bean.Condominio;
import com.cd.sis.bean.Morador;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;

/**
 *
 * @author Lettiery
 */
public class JavaFXUtil {

    
    // serve para o sistema chamar o programa padrao que abre esse tipo de arquivo
    public static void abrirArquivoDoSistema(File file) throws IOException{
        Desktop.getDesktop().open(file);    
    } 
    
    public static void colunValueMoedaFormat(TableColumn colunaDouble) {
        colunaDouble.setCellFactory(col
                -> new TableCell<Double, Number>() {
                    @Override
                    public void updateItem(Number price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(OperacaoStringUtil.formatarStringValorMoeda(price.doubleValue()));
                        }
                    }
                });
    }

    public static void colunValueQuantidadeFormat(TableColumn colunaDouble) {
        colunaDouble.setCellFactory(col
                -> new TableCell<Double, Number>() {
                    @Override
                    public void updateItem(Number price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(OperacaoStringUtil.formatarStringQuantidade(price.doubleValue()));
                        }
                    }
                });
    }
    
    public static void colunDataTimeFormat(TableColumn colunaDouble) {
        colunaDouble.setCellFactory(col
                -> new TableCell<Calendar, Calendar>() {
                    @Override
                    public void updateItem(Calendar price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(OperacaoStringUtil.formatDataTimeValor(price));
                        }
                    }
                });
    }
    public static void colunDataTimeFormatDate(TableColumn colunaDouble) {
        colunaDouble.setCellFactory((Object col)
                -> new TableCell<Date, Date>() {
                    @Override
                    public void updateItem(Date price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText( OperacaoStringUtil.formatDataTimeValor(price) );
                        }
                    }
                });
    }
    
    public static void colunDataFormatDate(TableColumn colunaDouble) {
        colunaDouble.setCellFactory((Object col)
                -> new TableCell<Date, Date>() {
                    @Override
                    public void updateItem(Date price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty || price == null) {
                            setText(null);
                        } else {
                            setText( OperacaoStringUtil.formatDataValor(price) );
                        }
                    }
                });
    }
    
    public static void colunDataMesFormat(TableColumn colunaDouble) {
        colunaDouble.setCellFactory(col
                -> new TableCell<Calendar, Calendar>() {
                    @Override
                    public void updateItem(Calendar price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(OperacaoStringUtil.formatDataMesValor(price));
                        }
                    }
                });
    }
    
    public static void colunDataMesFormatDate(TableColumn colunaDouble) {
        colunaDouble.setCellFactory(col
                -> new TableCell<Date, Date>() {
                    @Override
                    public void updateItem(Date price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(OperacaoStringUtil.formatDataMesValor(price));
                        }
                    }
                });
    }
    
    
    public static void nextFielOnAction(TextField action, Node foccus){
        action.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                foccus.requestFocus();
                if(foccus instanceof TextField){
                    ((TextField) foccus).selectAll();
                }else if(foccus instanceof Button){
                    ((Button) foccus).defaultButtonProperty().bind(((Button) foccus).focusedProperty());
                }
            }
        });
    }
    
    
    public static Calendar toDate(DatePicker datePicker) {
        if(datePicker.getValue() == null){
            return null;
        }
        LocalDate ld = datePicker.getValue();
        Instant instant = ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(Date.from(instant));
        return gc;
    }

    /**
     * Converte Date para LocalDate
     *
     * @param d
     * @return LocalDate
     */
    public static LocalDate toLocalDate(Calendar d) {
        Instant instant = Instant.ofEpochMilli(d.getTime().getTime());
        LocalDate localDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
        return localDate;
    }

    public static void beginFoccusTextField(TextField text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                text.requestFocus();
                text.selectAll();
            }
        });
    }

    public static void colunMoradorFormat(TableColumn colunaMorador) {
        colunaMorador.setCellFactory(col
                -> new TableCell<Morador, Morador>() {
                    @Override
                    public void updateItem(Morador price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            try{
                                setText(price.getNome());
                            }catch(NullPointerException ne){
                                setText(null);
                            }
                        }
                    }
                });
    }
    
    public static void colunUnidadeMoradorFormat(TableColumn colunaMorador) {
        colunaMorador.setCellFactory(col
                -> new TableCell<Morador, Morador>() {
                    @Override
                    public void updateItem(Morador price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            try{
                                setText(price.getUnidade());
                            }catch(NullPointerException ne){
                                setText(null);
                            }
                        }
                    }
                });
    }
    
    public static void colunCondominioFormat(TableColumn colunaCondominio) {
        colunaCondominio.setCellFactory(col
                -> new TableCell<Condominio, Condominio>() {
                    @Override
                    public void updateItem(Condominio price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            try{
                                setText(price.getNome());
                            }catch(NullPointerException ne){
                                setText(null);
                            }
                        }
                    }
                });
    }

    public static void colunEmailMoradorFormat(TableColumn colEmail) {
        colEmail.setCellFactory(col
                -> new TableCell<Morador, Morador>() {
                    @Override
                    public void updateItem(Morador price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            try{
                                setText(price.getEmail());
                            }catch(NullPointerException ne){
                                setText(null);
                            }
                        }
                    }
                });
    }
    
}
