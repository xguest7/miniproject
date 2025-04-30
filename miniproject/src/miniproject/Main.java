package miniproject;

import java.io.File;
import java.util.Scanner;

import net.coobird.thumbnailator.Thumbnails;

public class Main {

	public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("크기를 줄일 파일명을 입력해주세요:");
            String inputFileName = scanner.nextLine().trim();

            if (inputFileName.isEmpty()) {
                System.out.println("프로그램을 종료합니다.");
                break;
            }

            File inputFile = new File(inputFileName);

            if (!inputFile.exists()) {
                System.out.println("파일이 없습니다. 다시 입력해주세요");
                continue;
            }

            try {
                System.out.print("변환할 크기의 가로픽셀을 입력해주세요: ");
                int width = Integer.parseInt(scanner.nextLine().trim());

                System.out.print("변환할 크기의 세로픽셀을 입력해주세요: ");
                int height = Integer.parseInt(scanner.nextLine().trim());

                System.out.print("변환할 파일명을 입력해주세요: ");
                String outputFileName = scanner.nextLine().trim();

                // 확장자 자동 붙이기 (jpg)
                String outputFilePath = inputFile.getParent() + File.separator + outputFileName + ".jpg";

                Thumbnails.of(inputFile)
                          .size(width, height)
                          .toFile(outputFilePath);

                System.out.println("변환이 완료되었습니다.");
            } catch (Exception e) {
                System.out.println("오류가 발생되었습니다.");
                System.out.println(e.getMessage());
            }
        }

        scanner.close();
    }	

}
