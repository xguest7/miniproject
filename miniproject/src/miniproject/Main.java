package miniproject;

import javax.swing.*;

import net.coobird.thumbnailator.Thumbnails;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class Main extends JFrame {

    private JTextField widthField;
    private JTextField heightField;
    private JTextField outputNameField;
    private JLabel selectedFileLabel;
    private File selectedFile;

    public Main() {
        setTitle("이미지 리사이저");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 1));

        // 파일 선택
        JButton fileButton = new JButton("이미지 파일 선택");
        selectedFileLabel = new JLabel("선택된 파일 없음");
        fileButton.addActionListener(this::chooseFile);

        // 크기 입력
        JPanel sizePanel = new JPanel(new GridLayout(1, 4));
        widthField = new JTextField();
        heightField = new JTextField();
        sizePanel.add(new JLabel("가로(px):"));
        sizePanel.add(widthField);
        sizePanel.add(new JLabel("세로(px):"));
        sizePanel.add(heightField);

        // 저장 파일명 입력
        JPanel namePanel = new JPanel(new BorderLayout());
        outputNameField = new JTextField();
        namePanel.add(new JLabel("저장 파일명 (확장자 제외):"), BorderLayout.WEST);
        namePanel.add(outputNameField, BorderLayout.CENTER);

        // 변환 버튼
        JButton convertButton = new JButton("변환 실행");
        convertButton.addActionListener(this::convertImage);

        // 구성 요소 추가
        add(fileButton);
        add(selectedFileLabel);
        add(sizePanel);
        add(namePanel);
        add(convertButton);
    }

    private void chooseFile(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            selectedFileLabel.setText("선택된 파일: " + selectedFile.getAbsolutePath());
        }
    }

    private void convertImage(ActionEvent e) {
        if (selectedFile == null || !selectedFile.exists()) {
            showError("유효한 파일을 선택해주세요.");
            return;
        }

        String widthText = widthField.getText().trim();
        String heightText = heightField.getText().trim();
        String outputName = outputNameField.getText().trim();

        if (widthText.isEmpty() || heightText.isEmpty() || outputName.isEmpty()) {
            showError("모든 값을 입력해주세요.");
            return;
        }

        try {
            int width = Integer.parseInt(widthText);
            int height = Integer.parseInt(heightText);

            String outputPath = selectedFile.getParent() + File.separator + outputName + ".jpg";

            Thumbnails.of(selectedFile)
                      .size(width, height)
                      .keepAspectRatio(false) // 비율 유지 끔
                      .toFile(outputPath);

            JOptionPane.showMessageDialog(this, "변환이 완료되었습니다.");
        } catch (Exception ex) {
            showError("오류 발생: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "오류", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main gui = new Main();
            gui.setVisible(true);
        });
    }
}
