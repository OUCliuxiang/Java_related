package gui;

import com.GenerateExcel.Ticket2Excel;
import com.utils.PicUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

public class FileChooser {
    static String path;
    static String outputPath;
    static JTextArea infoPrint;
    static JTextField picPath, excelPath;
    static JPanel northPanel, centerPanel, southPanel;
/*
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                createWindow();
            }
        });
    }
*/
    public static void createWindow() {
        JFrame frame = new JFrame("创新中心发票填报辅助系统");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createNorthPanel();
        frame.add(northPanel, BorderLayout.NORTH);

        createSouthPanel();
        frame.add(southPanel, BorderLayout.SOUTH);

        createCenterPanel();
        frame.add(centerPanel, BorderLayout.CENTER);

        frame.setSize(500, 400);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void createNorthPanel(){
        File directory = new File("");
        path = directory.getAbsolutePath();

        picPath = new JTextField(path);
        excelPath = new JTextField(path);
        JButton picButton = createButton("输入", picPath);
        JButton excelButton = createButton("输出", excelPath);

        northPanel = new JPanel(new BorderLayout());
        JPanel labelPanel = new JPanel(new GridLayout(2,1));
        JPanel textPanel = new JPanel(new GridLayout(2,1));
        JPanel buttonPanel = new JPanel(new GridLayout(2,1));

        labelPanel.add(new JLabel("输入路径：", SwingConstants.RIGHT));
        labelPanel.add(new JLabel("输出路径：", SwingConstants.RIGHT));
        northPanel.add(labelPanel, BorderLayout.WEST);

        textPanel.add(picPath);
        textPanel.add(excelPath);
        northPanel.add(textPanel, BorderLayout.CENTER);

        buttonPanel.add(picButton);
        buttonPanel.add(excelButton);
        northPanel.add(buttonPanel, BorderLayout.EAST);
    }

    private static void createCenterPanel(){
        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new EtchedBorder());
        infoPrint = new JTextArea("转换信息：\n");
        infoPrint.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(infoPrint);
        // infoPrint.setEnabled(false);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private static JButton createButton(String name, JTextField textField){
        JButton button = new JButton("选择");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(path);
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(null);
                if(option == JFileChooser.APPROVE_OPTION){
                    File file = fileChooser.getSelectedFile();
                    textField.setText(file.toString());
                    if (name == "输入"){
                        FileChooser.path = textField.getText();
                        FileChooser.outputPath = textField.getText().replace(
                                "data", "output");
                        excelPath.setText(outputPath);
                    }
                    else if ( name == "输出"){
                        FileChooser.outputPath = textField.getText();
                    }
                }
                else{
                    textField.setText("请重新选择");
                }
            }
        });
        return button;
    }

    private static void createSouthPanel(){
        southPanel = new JPanel();
        southPanel.setLayout(new FlowLayout());
        JButton startButton = new JButton("开始");
        JButton stopButton = new JButton("退出");

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File files = new File(FileChooser.path);
                File[] fs = files.listFiles();
                for ( File f:fs){
                    String f_Name = f.toString();
                    if (! f.isDirectory() &&
                            ! f_Name.startsWith("compress", f_Name.length()-12)&& (
                            f_Name.startsWith("jpg", f_Name.length()-3) ||
                            f_Name.startsWith("JPG", f_Name.length()-3) ||
                            f_Name.startsWith("png", f_Name.length()-3) ||
                            f_Name.startsWith("PNG", f_Name.length()-3)))
                    {
                        PicUtils.commpressPicForScale(f.toString(),
                                f.toString().replace(".jpg", "_compress.jpg"),
                                300, 0.85);
                        infoPrint.append(String.format("%s 图片压缩成功。\n", f.toString()));
                        infoPrint.paintImmediately(infoPrint.getBounds());
                        int state = Ticket2Excel.submit2AliAPI(
                                f.toString().replace(".jpg", "_compress.jpg"), outputPath);
                        if (state != 200){
                            infoPrint.append(String.format("%s 失败，错误码%d。\n", f.toString(), state));
                            infoPrint.paintImmediately(infoPrint.getBounds());
                        }else{
                            infoPrint.append(String.format("%s 完成转换。\n", f.toString()));
                            long time = Ticket2Excel.getUsedTime();
                            infoPrint.append(String.format("用时%f 秒。\n", (double)time/1000.0));
                            infoPrint.paintImmediately(infoPrint.getBounds());
                        }
                    }
                    else{
                        infoPrint.append(String.format("%s 未处理，由于其不是可接受的文件。\n", f.toString()));
                        infoPrint.paintImmediately(infoPrint.getBounds());
                    }
                }
            }
        });
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                System.exit(0);
            }
        });;
        southPanel.add(startButton);
        southPanel.add(stopButton);
    }
}

