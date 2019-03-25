package com.wz.HttpUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.jsoup.select.Elements;
import org.omg.CORBA.PRIVATE_MEMBER;

import com.wz.bean.Novel;
import com.wz.bean.NovelMin;
import com.wz.bean.task;
import com.wz.dbmanager.BookService;

public class BookTask implements task{
	private String url;
	private RandomAccessFile fo;
	private Object[] objs;
	private Novel book;
	private Elements list;
	private File file;
	public BookTask(String url) {
		this.url=url;
		Object[] objs=Util.HandleBookMes(url);
		book=(Novel) objs[0];
		list=(Elements) objs[1];
		file=new File("BOOKS/"+book.getNovelName()+".txt");
	}
	
	public void startdownload() {
		try {
			
			if (file.exists()) {
				file.delete();
			}
			fo=new RandomAccessFile(file,"rw");
			System.out.println(book.toString()+"    "+"��ʼ����!");
			
			for(int i=0;i<list.size();i++) {
				String url=Util.baseurl+list.get(i).attr("href");
				String content=Util.getContentNovel(url);
				content.replace("<p><a href=\"http://koubei.baidu.com/s/xbiquge.la\" target=\"_blank\">��,�����ȥ,����������,����Խ�߸���Խ��,��˵���±�Ȥ������ֵ�����ҵ���Ư��������Ŷ!</a> �ֻ�վȫ�¸İ�������ַ��http://m.xbiquge.la�����ݺ���ǩ�����վͬ�����޹�������Ķ���</p>\r\n" + 
						"</div>", "");
				if (content.length()>2000) {
					fo.write(content.getBytes());
					if (i %50==0) {
						System.out.println(book.getNovelName()+"   ���ؽ���:"+(i)*100/list.size()+"%");
					}
				}
			}
			
			if (file.length()>5000) {
				Util.manager.sucessDownload(book);
				System.out.println(book.toString()+"    "+"���سɹ�!");
				int[] ids=Util.getIDS(url);
				BookService.addBook(new NovelMin(ids[0], ids[1],book.getChapterNumber(), book.getLastUpdate()));
				File file=new File("BOOKS/ADMIN/HISTORY.txt");
				RandomAccessFile fo=new RandomAccessFile(file, "rw");
				fo.seek(fo.length());
				fo.write(("��"+book.getNovelName()+"��"+"    ����ʱ��:"+timeUtil.getTime()+"\n").getBytes());
				fo.close();
			}else {
				System.err.println(book.toString()+"    "+"����ʧ��!");
				file.delete();
			}

		}catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				fo.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		
	}

	public boolean IsUpdate() {
		return false;
	}
	
	
}
