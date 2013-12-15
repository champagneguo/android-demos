package com.ckl.android.FileManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class FileManager extends ListActivity {
	private List<IconifiedText>	directoryEntries = new ArrayList<IconifiedText>();
	private List<IconifiedText>	fileEntries 	 = new ArrayList<IconifiedText>();
	private Comparator<IconifiedText> comparator = null;
	private File				currentDirectory = new File("/");
	private File 				myTmpFile 		 = null;
	private int 				myTmpOpt		 = -1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		browseToRoot();
		this.setSelection(0);

		//�����ִ�Сд�Ƚϣ������ļ��б�����
		comparator = new Comparator<IconifiedText>() {
			public int compare(IconifiedText a, IconifiedText b) {
				return a.getText().compareToIgnoreCase(b.getText()); 
			}
		};

		//���������˵�
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i(CONST.TAG, "long click");
				// ȡ��ѡ�е�һ����ļ���
				String selectedFileString = directoryEntries.get(position).getText();
				
				if (!dealRefreshAndUpLevel(selectedFileString)) {
					File file = new File(GetCurDirectory() + File.separator + selectedFileString);
					if (file != null) {
						fileOptMenu(file);
					}
				}
				
				return true;
			}
		});
	}
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			if (this.currentDirectory.getAbsolutePath().compareTo("/") != 0) {
				this.upOneLevel();
				return false;
			}
		}
		return super.onKeyUp(keyCode, event);
	}
	
	//����ļ�ϵͳ�ĸ�Ŀ¼
	private void browseToRoot() {
		browseTo(new File(File.separator));
    }
	//������һ��Ŀ¼
	private void upOneLevel() {
		if(this.currentDirectory.getParent() != null)
			this.browseTo(this.currentDirectory.getParentFile());
	}
	//���ָ����Ŀ¼
	private void browseTo(final File file) {
		if (file.isDirectory()) {
			File[] list = file.listFiles();
			if (null != list) {
				this.setTitle(file.getAbsolutePath());
				this.currentDirectory = file;
				fill(list);
			} else {
				Log.i(CONST.TAG, "no rights");
				CONST.DisplayToast(this, R.string.no_rights);
			}
		}
	}
	//��ָ���ļ�
	protected void openFile(File aFile) {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		File file = new File(aFile.getAbsolutePath());
		// ȡ���ļ���
		String fileName = file.getName();
		String type = null;
		
		// ���ݲ�ͬ���ļ����������ļ�
		if (checkEnds(fileName, R.array.fileEndingFlash)) {
			//todo:ֱ�Ӵ�flash
			return ;
		} else if (checkEnds(fileName, R.array.fileEndingImage)) {
			type = "image/*";
		} else if (checkEnds(fileName, R.array.fileEndingAudio)) {
			type = "audio/*";
		} else if (checkEnds(fileName, R.array.fileEndingVideo)) {
			type = "video/*";
		} else if (checkEnds(fileName, R.array.fileEndingText)) {
			type = "text/*";
		}
		
		if(type != null) {
			intent.setDataAndType(Uri.fromFile(file), type);
		}
		startActivity(intent);
	}
	//����������Ϊ����ListActivity��Դ
	private void fill(File[] files) {		
		if (null == files)
		{
			return ;
		}
		
		//����б�
		this.directoryEntries.clear();
		fileEntries.clear();

		//���һ����ǰĿ¼��ѡ��
		this.directoryEntries.add(new IconifiedText(getString(R.string.current_dir), getResources().getDrawable(R.drawable.reload)));
		//������Ǹ�Ŀ¼�������һ��Ŀ¼��
		if (this.currentDirectory.getParent() != null)
			this.directoryEntries.add(new IconifiedText(getString(R.string.up_one_level), getResources().getDrawable(R.drawable.back)));

		Drawable currentIcon = null;
		for (File currentFile : files) {
			//�ж���һ���ļ��л���һ���ļ�
			if (currentFile.isDirectory()) {
				currentIcon = getResources().getDrawable(R.drawable.folder);
			} else {
				//ȡ���ļ���
				String fileName = currentFile.getName();
				//�����ļ������ж��ļ����ͣ����ò�ͬ��ͼ��
				currentIcon = getIcon(fileName);
			}
			//ȷ��ֻ��ʾ�ļ���������ʾ·���磺/sdcard/111.txt��ֻ����ʾ111.txt
			String path = this.currentDirectory.getAbsolutePath();
			int lenght = path.length();
			if (!path.endsWith(File.separator)) {
				lenght++;
			}
			if (currentFile.isDirectory()) {
				this.directoryEntries.add(new IconifiedText(currentFile.getAbsolutePath().substring(lenght), currentIcon));
			} else {
				fileEntries.add(new IconifiedText(currentFile.getAbsolutePath().substring(lenght), currentIcon));
			}
		}
		Collections.sort(fileEntries, comparator);
		Collections.sort(this.directoryEntries, comparator);
		for (int i = 0; i < fileEntries.size(); i++) {
			this.directoryEntries.add(fileEntries.get(i));
		}
		IconifiedTextListAdapter itla = new IconifiedTextListAdapter(this);
		//�������õ�ListAdapter��
		itla.setListItems(this.directoryEntries);
		//ΪListActivity���һ��ListAdapter
		this.setListAdapter(itla);
	}
	
	//ˢ�º���һ��Ŀ¼����
	private boolean dealRefreshAndUpLevel(String filename) {
		boolean dealed = false;
		
		if (filename.equals(getString(R.string.current_dir))) {
			dealed = true;
			//���ѡ�е���ˢ��
			this.browseTo(this.currentDirectory);
		} else if (filename.equals(getString(R.string.up_one_level))) {
			dealed = true;
			//������һ��Ŀ¼
			this.upOneLevel();
		}
		
		return dealed;
	}
	
	//����ֱ�Ӵ��ļ���Ŀ¼
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.i(CONST.TAG, "click");
		super.onListItemClick(l, v, position, id);
		// ȡ��ѡ�е�һ����ļ���
		String selectedFileString = this.directoryEntries.get(position).getText();
		
		if (!dealRefreshAndUpLevel(selectedFileString)) {
			File clickedFile = new File(this.currentDirectory.getAbsolutePath()+ File.separator + selectedFileString);
			
			if(clickedFile != null){
				if(clickedFile.isDirectory()){
					this.browseTo(clickedFile);
				} else {
					openFile(clickedFile);
				}
			}
		}
	}

	//ͨ���ļ����ж���ʲô���͵��ļ�
	private boolean checkEndsWithInStringArray(String checkItsEnd, String[] fileEndings) {
		for(String aEnd : fileEndings) {
			if(checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}
	
	private boolean checkEnds(String fileName, int id) {
		return checkEndsWithInStringArray(fileName, getResources().getStringArray(id));
	}
	
	//�����ļ������ж��ļ����ͣ����ò�ͬ��ͼ��
	private Drawable getIcon(String fileName) {
		int id = R.drawable.unkown;
		
		if (checkEnds(fileName, R.array.fileEndingImage)) {
			id = R.drawable.image;
		} else if (checkEnds(fileName, R.array.fileEndingWebText)) {
			id = R.drawable.webtext;
		} else if (checkEnds(fileName, R.array.fileEndingPackage)) {
			id = R.drawable.packed;
		} else if (checkEnds(fileName, R.array.fileEndingAudio)) {
			id = R.drawable.audio;
		} else if (checkEnds(fileName, R.array.fileEndingVideo)) {
			id = R.drawable.video;
		} else if (checkEnds(fileName, R.array.fileEndingText)) {
			id = R.drawable.text;
		} else if (checkEnds(fileName, R.array.fileEndingFlash)) {
			id = R.drawable.flash;
		}
		
		return getResources().getDrawable(id);
	}
	
	//��Ŀ¼����ĵ����˵�
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, R.string.new_folder).setIcon(R.drawable.addfolderr);
		//menu.add(0, 1, 0, "ɾ��Ŀ¼").setIcon(R.drawable.delete);//�����ɾ����ǰĿ¼�����ã��������ε�
		menu.add(0, 2, 0, R.string.paste).setIcon(R.drawable.paste);
		menu.add(0, 3, 0, R.string.root).setIcon(R.drawable.goroot);
		menu.add(0, 4, 0, R.string.up).setIcon(R.drawable.uponelevel);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case 0:
				Mynew();
				break;
//			case 1:
//				//ע�⣺ɾ��Ŀ¼�������������������ṩ��
//				//deleteFile��ɾ���ļ�����deleteFolder��ɾ������Ŀ¼��
//				MyDelete();
//				break;
			case 2:
				MyPaste();
				break;
			case 3:
				this.browseToRoot();
				break;
			case 4:
				this.upOneLevel();
				break;
		}
		return false;
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}
	
	void CopyOrCut(File src, File dst, int option) {
		if (option == 0) {//����ճ��
			copyFile(src, dst);
		} else if (option == 1){//����ճ��
			src.renameTo(dst);
			myTmpFile = null;//���Դ·��
		}
		browseTo(new File(GetCurDirectory()));
	}
	
	//ճ������
	public void MyPaste() {
		if ( myTmpFile == null ) {
			CONST.DisplayToast(FileManager.this, R.string.please_copy);
		} else {
			final File destFile = new File(GetCurDirectory()+File.separator+myTmpFile.getName());
			if (myTmpFile.isDirectory()) {
				if (destFile.exists()) {
					CONST.DisplayToast(FileManager.this, R.string.folder_exist);
				} else {
					copyDirectiory(myTmpFile, destFile);
					browseTo(new File(GetCurDirectory()));
				}
			} else {
				if(destFile.exists()) {
					Builder builder = new Builder(FileManager.this);
					builder.setTitle(R.string.paste_hint);
					builder.setMessage(R.string.cover_it);
					builder.setPositiveButton(android.R.string.ok,
							new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									CopyOrCut(myTmpFile, destFile, myTmpOpt);
								}
							});
					builder.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							});
					builder.setCancelable(false);
					builder.create();
					builder.show();
				} else {
					CopyOrCut(myTmpFile, destFile, myTmpOpt);
				}
			}
		}
	}
	
	//ɾ�������ļ���[�����ɾ����ǰĿ¼������]
	public void MyDelete() {
		//ȡ�õ�ǰĿ¼
		File tmp=new File(this.currentDirectory.getAbsolutePath());
		//������һ��Ŀ¼
		this.upOneLevel();
		//ɾ��ȡ�õ�Ŀ¼
		if ( deleteFolder(tmp) ) {
			//CONST.DisplayToast(FileManager.this, R.string.delete_ok);
		} else {
			CONST.DisplayToast(FileManager.this, R.string.delete_error);
		}
		this.browseTo(this.currentDirectory);	
	}
	
	//�½��ļ���
	public void Mynew() {
		final LayoutInflater factory = LayoutInflater.from(FileManager.this);
		final View dialogview = factory.inflate(R.layout.dialog, null);
		//����TextView
		((TextView) dialogview.findViewById(R.id.TextView_PROM)).setText(R.string.input_folder_name);
		//����EditText
		((EditText) dialogview.findViewById(R.id.EditText_PROM)).setText(R.string.folder_name);
		
		Builder builder = new Builder(FileManager.this);
		builder.setTitle(R.string.new_folder);
		builder.setView(dialogview);
		builder.setPositiveButton(android.R.string.ok,
				new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String value = ((EditText) dialogview.findViewById(R.id.EditText_PROM)).getText().toString();
						if ( newFolder(value) ) {
							//CONST.DisplayToast(FileManager.this, R.string.new_folder_ok);
						}else{
							CONST.DisplayToast(FileManager.this, R.string.new_folder_error);	
						}
					}
				});
		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						dialog.cancel();
					}
				});
		builder.show();
	}
	
	boolean mkDirs(File dir) {
		boolean creadok = dir.mkdirs();
		if (creadok){
			this.browseTo(this.currentDirectory);
			return true;
		}else{
			return false;
		}
	}
	
	//�½��ļ���
	public boolean newFolder(String file) {
		boolean success = true;
		File dirFile = new File(this.currentDirectory.getAbsolutePath()+File.separator+file);
		try {
			if (dirFile.exists()) {
				if (dirFile.isDirectory()) {
					CONST.DisplayToast(FileManager.this, R.string.folder_exist);
					success = false;
				} else {
					success = mkDirs(dirFile);
				}	
			} else {
				success = mkDirs(dirFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
			return false;
		}
		return success;
	}
	//ɾ���ļ�
    public boolean deleteFile(File file) {
		boolean result = false;
		if (file != null) {
			try {
				File file2 = file;
				file2.delete();
				result = true;
			} catch (Exception e) {
				e.printStackTrace();
				result = false;
			}
		}
		return result;
	} 
    //ɾ���ļ���
	public boolean deleteFolder(File folder) {
		boolean result = false;
		try {
			String childs[] = folder.list();
			if (childs == null || childs.length <= 0) {
				if (folder.delete()) {
					result = true;
				}
			} else {
				for (int i = 0; i < childs.length; i++) {
					String childName = childs[i];
					String childPath = folder.getPath() + File.separator + childName;
					File filePath = new File(childPath);
					if (filePath.exists() && filePath.isFile()) {
						if (filePath.delete()) {
							result = true;
						} else {
							result = false;
							break;
						}
					} else if (filePath.exists() && filePath.isDirectory()) {
						if (deleteFolder(filePath)) {
							result = true;
						} else {
							result = false;
							break;
						}
					}
				}
				folder.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	} 
	
	//�����ļ��������򿪣��������Ȳ���
	public void fileOptMenu(final File file) {
		OnClickListener listener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {//��
					if(file.isDirectory()){
						browseTo(file);
					} else {
						openFile(file);
					}
				} else if (which == 1) {//������
					//�Զ���һ��������ĶԻ�����TextView��EditText����
					final LayoutInflater factory = LayoutInflater.from(FileManager.this);
					final View dialogview = factory.inflate(R.layout.rename, null);
					//����TextView����ʾ��Ϣ
					((TextView) dialogview.findViewById(R.id.TextView01)).setText(R.string.rename);
					//����EditText������ʼֵ
					((EditText) dialogview.findViewById(R.id.EditText01)).setText(file.getName());
					
					Builder builder = new Builder(FileManager.this);
					builder.setTitle(R.string.rename);
					builder.setView(dialogview);
					builder.setPositiveButton(android.R.string.ok,
							new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									//���ȷ��֮��
									final String value = GetCurDirectory()+File.separatorChar
										+((EditText) dialogview.findViewById(R.id.EditText01)).getText().toString();
									File file1 = new File(value);
									if(file1.exists()){
										if (file1.isDirectory()){
											CONST.DisplayToast(FileManager.this, R.string.folder_exist);
										} else {
											Builder builder = new Builder(FileManager.this);
											builder.setTitle(R.string.rename);
											builder.setMessage(R.string.cover_it);
											builder.setPositiveButton(android.R.string.ok,
													new AlertDialog.OnClickListener() {
														public void onClick(DialogInterface dialog, int which) {
															file.renameTo(new File(value));
															browseTo(new File(GetCurDirectory()));
														}
													});
											builder.setNegativeButton(android.R.string.cancel,
													new DialogInterface.OnClickListener() {
														public void onClick(DialogInterface dialog, int which) {
															dialog.cancel();
														}
													});
											builder.setCancelable(false);
											builder.create();
											builder.show();
										}
									}else{
										file.renameTo(file1);
										browseTo(new File(GetCurDirectory()));
									}
								}
							});
					builder.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							});
					builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
								public void onCancel(DialogInterface dialog) {
									dialog.cancel();
								}
							});
					builder.show();
				} else if ( which == 2 ) {//ɾ��
					Builder builder = new Builder(FileManager.this);
					builder.setTitle(R.string.delete);
					builder.setMessage(getResources().getString(R.string.sure_delete) + file.getName() + "��");
					builder.setPositiveButton(android.R.string.ok,
							new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									boolean ret;
									if (file.isDirectory()) {
										ret = deleteFolder(file);
									} else {
										ret = deleteFile(file);
									}
									if (ret) {
										browseTo(new File(GetCurDirectory()));
										//CONST.DisplayToast(FileManager.this, R.string.delete_ok);
									} else {
										CONST.DisplayToast(FileManager.this, R.string.delete_error);
									}
								}
							});
					builder.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							});
					builder.setCancelable(false);
					builder.create();
					builder.show();
				} else if ( which == 3 ) {//����
					//�������Ǹ��Ƶ��ļ�Ŀ¼
					myTmpFile = file;
					//����������0��ʾ���Ʋ���
					myTmpOpt = 0;
				} else if ( which == 4 ) {//����
					//�������Ǹ��Ƶ��ļ�Ŀ¼
					myTmpFile = file;
					//����������0��ʾ���в���
					myTmpOpt = 1;	 
				}
			}
		};
		//��ʾ�����˵�
		Builder option = new AlertDialog.Builder(FileManager.this);
		option.setTitle(R.string.select_option);
		if (file.isDirectory()){
			option.setItems(R.array.dirmenu,listener);
		} else {
			option.setItems(R.array.filemenu,listener);
		}
        option.show();
	}
	//�õ���ǰĿ¼�ľ���·��
	public String GetCurDirectory() {
		return this.currentDirectory.getAbsolutePath();
	}
	//�ƶ��ļ�
	public void moveFile(String source, String destination) {
		new File(source).renameTo(new File(destination));   
	}
	//�����ļ�
	public void copyFile(File src, File target) {
		InputStream input = null;
		BufferedInputStream inBuff = null;
		
		OutputStream output = null;
		BufferedOutputStream outBuff = null;
		
		try {
			// �½��ļ����������������л���   
			input = new FileInputStream(src);
			inBuff = new BufferedInputStream(input);

	        // �½��ļ���������������л���   
	        output = new FileOutputStream(target);
	        outBuff = new BufferedOutputStream(output);
	        
	        // ��������
	        byte[] b = new byte[1024 * 50];
	        int len;
	        while ((len = inBuff.read(b)) != -1) {
	            outBuff.write(b, 0, len);
	        }
	        // ˢ�´˻���������   
	        outBuff.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inBuff != null){
					inBuff.close();
				}
				if (outBuff != null){
					outBuff.close();
				}
				if (input != null){
					input.close();
				}
				if (output != null){
					output.close();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
    // �����ļ���   
    public void copyDirectiory(File sourceDir, File targetDir) {
        // �½�Ŀ��Ŀ¼
    	targetDir.mkdirs();
        // ��ȡԴ�ļ��е�ǰ�µ��ļ���Ŀ¼
        File[] file = sourceDir.listFiles();
	    if (file != null) {
	        for (int i = 0; i < file.length; i++) {
	            if (file[i].isDirectory()) {
	                // ׼�����Ƶ�Դ�ļ���
	            	File dir1 = new File(sourceDir.getAbsolutePath() + File.separator + file[i].getName());
	                // ׼�����Ƶ�Ŀ���ļ���
	            	File dir2 = new File(targetDir.getAbsolutePath() + File.separator + file[i].getName());
	                copyDirectiory(dir1, dir2);  
	            } else if (file[i].isFile()) {
	                // Դ�ļ�
	                File sourceFile = file[i];
	                // Ŀ���ļ�
	                File targetFile = new File(targetDir.getAbsolutePath() + File.separator + file[i].getName());
	                copyFile(sourceFile, targetFile);
	            }
	        }
        } else {
        	CONST.DisplayToast(FileManager.this, R.string.no_rights);
        }
    }
}
