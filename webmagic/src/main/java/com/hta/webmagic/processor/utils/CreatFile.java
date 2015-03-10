package com.hta.webmagic.processor.utils;

import java.io.File;
import java.io.IOException;

public class CreatFile {
	public static boolean createFile(String destFileName) {
        File file = new File(destFileName);
        if(file.exists()) {
            System.out.println("���������ļ�" + destFileName + "ʧ�ܣ�Ŀ���ļ��Ѵ��ڣ�");
            return false;
        }
        if (destFileName.endsWith(File.separator)) {
            System.out.println("���������ļ�" + destFileName + "ʧ�ܣ�Ŀ���ļ�����ΪĿ¼��");
            return false;
        }
        //�ж�Ŀ���ļ����ڵ�Ŀ¼�Ƿ����
        if(!file.getParentFile().exists()) {
            //���Ŀ���ļ����ڵ�Ŀ¼�����ڣ��򴴽���Ŀ¼
            System.out.println("Ŀ���ļ�����Ŀ¼�����ڣ�׼��������");
            if(!file.getParentFile().mkdirs()) {
                System.out.println("����Ŀ���ļ�����Ŀ¼ʧ�ܣ�");
                return false;
            }
        }
        //����Ŀ���ļ�
        try {
            if (file.createNewFile()) {
                System.out.println("���������ļ�" + destFileName + "�ɹ���");
                return true;
            } else {
                System.out.println("���������ļ�" + destFileName + "ʧ�ܣ�");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("���������ļ�" + destFileName + "ʧ�ܣ�" + e.getMessage());
            return false;
        }
    }
}
