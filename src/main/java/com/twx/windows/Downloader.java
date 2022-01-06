package com.twx.windows;

import com.twx.client.M3U8Downloader;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Downloader{


    private JTextField dirText=new JTextField(25);
    private JTextField urlValue=new JTextField(25);
    private JTextField fileName=new JTextField(25);

    public static void main(String[] args) {
        Downloader downloader = new Downloader();
        downloader.init();
    }

    public void init() {
        //1. 新建JFrame
        JFrame jf = new JFrame("m3u8下载器");

        //2. 新建JPanel
        JPanel jp = new JPanel();

        //下载链接
        JLabel urlName = new JLabel("下载地址");
        jp.add(urlName);
        jp.add(urlValue);

        //3. 新建标签、输入框、按钮
        //4. 将元素添加到JPanel里
        JLabel label=new JLabel("保存文件夹：");
        JButton button=new JButton("浏览");

        jp.add(label);
        jp.add(dirText);
        jp.add(button);

        //6. 下载文件名
        JLabel fileNameLabel = new JLabel("保存文件名：");
        jp.add(fileNameLabel);
        jp.add(fileName);

        //下载按钮
        JButton downBtn = new JButton("下载");
        jp.add(downBtn);
        downBtn.addActionListener(event -> {
            String url = urlValue.getText();
            String dir = dirText.getText();
            String fileNameText = fileName.getText();
            System.out.println("url: "+url);
            System.out.println("dir: "+dir);
            System.out.println("filename: "+fileNameText);
            M3U8Downloader m3U8Downloader = new M3U8Downloader(urlValue.getText(), dirText.getText(), fileName.getText());
            m3U8Downloader.start();
        });

        //日记窗口
        JTextArea jta=new JTextArea("",20,50);
        jta.setLineWrap(true);    //设置文本域中的文本为自动换行
        jta.setForeground(Color.BLACK);    //设置组件的背景色
        jta.setFont(new Font("楷体",Font.BOLD,16));    //修改字体样式

        //滚动窗口
        JScrollPane jsp=new JScrollPane(jta);    //将文本域放入滚动窗口

        Dimension size=jta.getPreferredSize();    //获得文本域的首选大小
        jsp.setBounds(0,90,size.width,size.height);

        jp.add(jsp);

        //5. 将面板添加到frame中
        jf.add(jp);

        jf.setVisible(true);
//        jf.setSize(500,300);
        jf.setBounds(300,300,1000,500);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//        jf.pack();    //自动调整大小

        button.addActionListener(new FileSelectListener());
    }


    class FileSelectListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0)
        {
            JFileChooser fc=new JFileChooser(FileUtils.getUserDirectoryPath());

            //选择文件夹
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY | JFileChooser.SAVE_DIALOG);

            int val=fc.showOpenDialog(null);    //文件打开对话框
            if(val==fc.APPROVE_OPTION)
            {
                //正常选择文件
                dirText.setText(fc.getSelectedFile().toString());
            }
            else
            {
                //未正常选择文件，如选择取消按钮
                dirText.setText("未选择文件");
            }
        }
    }
}
