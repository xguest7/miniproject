package miniproject;

import java.io.File;
import java.util.Scanner;

import net.coobird.thumbnailator.Thumbnails;

public class CWS {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		
		while(true) {
			System.out.print("크기를 줄일 파일명을 입력해주세요:");
			
			String filename = scan.nextLine();
			
			File file = new File(filename);
			
			if(filename.equals("")) {
				scan.close();
				break;
			}
			
			if(!file.exists())
				System.out.println("파일이 없습니다. 다시 입력해주세요");
			else {
				System.out.print("변환할 크기의 가로픽셀을 입력해주세요: ");
				
				int a = scan.nextInt();
				scan.nextLine();
				
				System.out.print("변환할 크기의 세로픽셀을 입력해주세요: ");
				
				int b = scan.nextInt();
				scan.nextLine();
				
				System.out.print("변환할 파일명을 입력해주세요:");
				
				String c = scan.nextLine();
				
				try {
					Thumbnails.of(filename)
					.size(a,b)
					.keepAspectRatio(false)
					.toFile(c+".jpg");
				} catch (Exception e) {
					System.out.println("오류가 발생되었습니다.");
					scan.close();
					break;
				}
				System.out.println("변환이 완료되었습니다.");
				scan.close();
				break;
			}
			
		}

	}

}
