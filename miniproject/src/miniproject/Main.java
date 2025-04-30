package miniproject;
import net.coobird.thumbnailator.Thumbnails;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.io.File;
import java.util.List;

public class Main extends JFrame {

    private JTextField widthField;
    private JTextField heightField;
    private JTextField outputNameField;
    private JLabel selectedFileLabel;
    private File selectedFile;
    private JProgressBar progressBar;

    public Main() {
        setTitle("이미지 리사이저 (Drag & Drop / 선택 지원)");
        setSize(550, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙 배치
        setLayout(new BorderLayout(10, 10));

        // 상단 - 드래그 및 파일 선택
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        JButton fileSelectButton = new JButton("파일 선택");
        fileSelectButton.setPreferredSize(new Dimension(120, 30));
        fileSelectButton.addActionListener(e -> chooseFile());

        JPanel dragPanel = new JPanel();
        dragPanel.setPreferredSize(new Dimension(400, 60));
        dragPanel.setBackground(new Color(230, 230, 250));
        dragPanel.setBorder(BorderFactory.createTitledBorder("여기에 파일을 드래그하세요"));
        selectedFileLabel = new JLabel("선택된 파일 없음");
        dragPanel.add(selectedFileLabel);

        new DropTarget(dragPanel, new DropTargetAdapter() {
            public void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable transferable = evt.getTransferable();
                    List<File> droppedFiles = (List<File>)
                            transferable.getTransferData(DataFlavor.javaFileListFlavor);
                    if (!droppedFiles.isEmpty()) {
                        setSelectedFile(droppedFiles.get(0));
                    }
                } catch (Exception ex) {
                    showError("파일을 불러오는 중 오류 발생: " + ex.getMessage());
                }
            }
        });

        topPanel.add(fileSelectButton, BorderLayout.WEST);
        topPanel.add(dragPanel, BorderLayout.CENTER);

        // 중앙 - 입력 필드들
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(3, 2, 10, 10));

        widthField = new JTextField();
        heightField = new JTextField();
        outputNameField = new JTextField();

        centerPanel.add(new JLabel("가로(px):", SwingConstants.RIGHT));
        centerPanel.add(widthField);
        centerPanel.add(new JLabel("세로(px):", SwingConstants.RIGHT));
        centerPanel.add(heightField);
        centerPanel.add(new JLabel("저장 파일명:", SwingConstants.RIGHT));
        centerPanel.add(outputNameField);

        // 하단 - 변환 버튼 & 진행바
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        JButton convertButton = new JButton("이미지 변환");
        convertButton.setPreferredSize(new Dimension(120, 30));
        convertButton.addActionListener(e -> convertImage());

        bottomPanel.add(progressBar, BorderLayout.CENTER);
        bottomPanel.add(convertButton, BorderLayout.EAST);

        // 전체 구성
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            setSelectedFile(chooser.getSelectedFile());
        }
    }

    private void setSelectedFile(File file) {
        this.selectedFile = file;
        selectedFileLabel.setText("선택된 파일: " + file.getName());

        // 자동으로 파일명 기본값 설정
        if (outputNameField.getText().isEmpty()) {
            String name = file.getName();
            int dot = name.lastIndexOf('.');
            if (dot > 0) name = name.substring(0, dot);
            outputNameField.setText(name + "_resized");
        }
    }

    private void convertImage() {
        if (selectedFile == null || !selectedFile.exists()) {
            showError("이미지 파일을 선택하거나 드래그해주세요.");
            return;
        }

        String widthText = widthField.getText().trim();
        String heightText = heightField.getText().trim();
        String outputName = outputNameField.getText().trim();

        if (widthText.isEmpty() || heightText.isEmpty() || outputName.isEmpty()) {
            showError("모든 필드를 입력해주세요.");
            return;
        }

        try {
            int width = Integer.parseInt(widthText);
            int height = Integer.parseInt(heightText);
            String outputPath = selectedFile.getParent() + File.separator + outputName + ".jpg";

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    progressBar.setIndeterminate(true);
                    Thumbnails.of(selectedFile)
                            .size(width, height)
                            .keepAspectRatio(false)
                            .toFile(outputPath);
                    return null;
                }

                @Override
                protected void done() {
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(100);
                    try {
                        get();
                        JOptionPane.showMessageDialog(Main.this, "변환이 완료되었습니다.");
                    } catch (Exception ex) {
                        showError("변환 중 오류 발생: " + ex.getMessage());
                    }
                }
            };
            worker.execute();

        } catch (NumberFormatException e) {
            showError("가로/세로는 숫자로 입력해주세요.");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "오류", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            // 윈도우 스타일 적용
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // 무시하고 기본 테마 사용
        }

        SwingUtilities.invokeLater(() -> {
            Main gui = new Main();
            gui.setVisible(true);
        });
    }
}
