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
        setTitle("이미지 리사이저 (Drag & Drop 지원)");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(7, 1));

        // 드래그 앤 드롭 영역
        JPanel dropPanel = new JPanel();
        dropPanel.setBackground(Color.LIGHT_GRAY);
        dropPanel.setBorder(BorderFactory.createTitledBorder("여기에 이미지 파일을 드래그하세요"));
        selectedFileLabel = new JLabel("선택된 파일 없음");
        dropPanel.add(selectedFileLabel);
        add(dropPanel);

        new DropTarget(dropPanel, new DropTargetAdapter() {
            public void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable transferable = evt.getTransferable();
                    List<File> droppedFiles = (List<File>)
                            transferable.getTransferData(DataFlavor.javaFileListFlavor);
                    if (!droppedFiles.isEmpty()) {
                        selectedFile = droppedFiles.get(0);
                        selectedFileLabel.setText("선택된 파일: " + selectedFile.getAbsolutePath());
                    }
                } catch (Exception ex) {
                    showError("파일을 불러오는 중 오류 발생: " + ex.getMessage());
                }
            }
        });

        // 크기 입력
        JPanel sizePanel = new JPanel(new GridLayout(1, 4));
        widthField = new JTextField();
        heightField = new JTextField();
        sizePanel.add(new JLabel("가로(px):"));
        sizePanel.add(widthField);
        sizePanel.add(new JLabel("세로(px):"));
        sizePanel.add(heightField);
        add(sizePanel);

        // 파일명 입력
        JPanel namePanel = new JPanel(new BorderLayout());
        outputNameField = new JTextField();
        namePanel.add(new JLabel("저장 파일명:"), BorderLayout.WEST);
        namePanel.add(outputNameField, BorderLayout.CENTER);
        add(namePanel);

        // 진행바
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        add(progressBar);

        // 변환 버튼
        JButton convertButton = new JButton("변환 실행");
        convertButton.addActionListener(e -> convertImage());
        add(convertButton);
    }

    private void convertImage() {
        if (selectedFile == null || !selectedFile.exists()) {
            showError("이미지 파일을 드래그하거나 선택해주세요.");
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

            // 비동기 처리로 UI 멈춤 방지
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    progressBar.setValue(0);
                    progressBar.setIndeterminate(true); // 진행 중 표시
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
                        get(); // 예외 확인
                        JOptionPane.showMessageDialog(Main.this, "변환이 완료되었습니다.");
                    } catch (Exception ex) {
                        showError("변환 중 오류 발생: " + ex.getMessage());
                    }
                }
            };

            worker.execute();
        } catch (NumberFormatException e) {
            showError("가로와 세로는 숫자로 입력해야 합니다.");
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
