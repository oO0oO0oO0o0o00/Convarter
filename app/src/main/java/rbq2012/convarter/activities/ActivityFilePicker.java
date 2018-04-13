package rbq2012.convarter.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Px;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Stack;

import rbq2012.convarter.R;

public class ActivityFilePicker extends AppCompatActivity implements DialogInterface.OnClickListener {

	final static public String INTENT_KEY_FILE = "file";
	final static public int REQ_CODE = 8616;
	final static public String SPREF_KEY_SORTBY = "sort_by";

	private FilesAdapter m_list_adpter;
	private ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_picker);
		list = findViewById(R.id.list);
		SharedPreferences spref = getPreferences(MODE_PRIVATE);
		int sortby = spref.getInt(SPREF_KEY_SORTBY, 0);
		if (sortby < 0 || sortby > 3) sortby = 0;
		m_list_adpter = new FilesAdapter(Environment.getExternalStorageDirectory(),
				new String[]{".js", ".txt"}, sortby);
		list.setOnItemClickListener(m_list_adpter);
		list.setAdapter(m_list_adpter);
		setResult(RESULT_CANCELED);
	}

	private void setScroll(int scroll) {
		list.setScrollY(scroll);
	}

	private @Px
	int getScroll() {
		return list.getScrollY();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_file_picker_options, menu);
		return true;
		//return super.onCreateOptionsMenu(menu);
	}

	public void refresh(MenuItem mi) {
		m_list_adpter.update();
	}

	public void sortby(MenuItem mi) {
		AlertDialog dia = new AlertDialog.Builder(this)
				.setItems(R.array.filepicker_sortby_list, this)
				.create();
		dia.show();
	}

	@Override
	public void onBackPressed() {
		if (m_list_adpter.back()) return;
		super.onBackPressed();
	}

	@Override
	public void onClick(DialogInterface dialogInterface, int i) {
		m_list_adpter.setSortby(i);
		SharedPreferences spref = getPreferences(MODE_PRIVATE);
		spref.edit().putInt(SPREF_KEY_SORTBY, i).apply();
	}

	private class FilesAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

		private Stack<DirInfo> stack;
		private DirInfo current;
		private FileInfo[] files;
		private LayoutInflater inflator;
		private String[] extensions;
		private int sortby;

		public FilesAdapter(File root, String[] extensions, int sortby) {
			stack = new Stack<>();
			current = new DirInfo(root);
			inflator = LayoutInflater.from(ActivityFilePicker.this);
			this.extensions = extensions;
			this.sortby = sortby;
			update();
		}

		public void setSortby(int sortby) {
			if (this.sortby == sortby) return;
			this.sortby = sortby;
			update();
		}

		public void update() {
			FileInfo[] infos;
			File dir = current.file;
			File[] files = dir.listFiles(new ExtFilter());
			int size = 0;
			if (files != null) size = files.length;
			boolean show_up_dir = true;
			if (dir.equals(Environment.getExternalStorageDirectory())) show_up_dir = false;
			infos = new FileInfo[size + (show_up_dir ? 1 : 0)];
			for (int i = 0; i < size; i++) {
				File f = files[i];
				infos[i] = new FileInfo(f);
			}
			if (show_up_dir) infos[size] = new FileInfo();
			Arrays.sort(infos);
			this.files = infos;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return files.length;
		}

		@Override
		public FileInfo getItem(int i) {
			return files[i];
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			view = inflator.inflate(R.layout.entry_filepicker_list, viewGroup, false);
			LinearLayout ll = (LinearLayout) view;
			FileInfo info = getItem(i);
			ImageView iv = (ImageView) ll.getChildAt(0);
			iv.setImageResource(info.type == 0 ? R.drawable.ic_file : R.drawable.ic_folder);
			TextView tv = (TextView) ll.getChildAt(1);
			tv.setText(info.name);
			return view;
		}

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
			FileInfo item = getItem(i);
			if (item.type == 100) {
				back();
				return;
			}
			current.scroll = getScroll();
			stack.push(current);
			if (item.type == 1) {
				current = new DirInfo(new File(current.file, item.name));
				update();
				return;
			}
			ActivityFilePicker.this.setResult(RESULT_OK, new Intent().putExtra(INTENT_KEY_FILE, item.file.getAbsolutePath()));
			ActivityFilePicker.this.finish();
		}

		public boolean back() {
			if (stack.isEmpty()) {
				File root = Environment.getExternalStorageDirectory();
				if (current.file.equals(root)) return false;
				current = new DirInfo(root);
				update();
			}
			current = stack.pop();
			update();
			setScroll(current.scroll);
			return true;
		}

		private class DirInfo {
			public File file;
			public @Px
			int scroll;

			public DirInfo(File file) {
				this.file = file;
				this.scroll = 0;
			}
		}

		private class ExtFilter implements FileFilter {
			@Override
			public boolean accept(File file) {
				if (file.isDirectory()) return true;
				String name = file.getName();
				for (String ext : extensions) {
					if (name.endsWith(ext)) return true;
				}
				return false;
			}
		}

		private class FileInfo implements Comparable {
			public String name;
			public File file;
			public int type;

			public FileInfo(File file) {
				this.file = file;
				this.name = file.getName();
				if (file.isDirectory()) type = 1;
				else type = 0;
			}

			public FileInfo() {
				this.file = null;
				this.type = 100;//Parent directory.
				this.name = getString(R.string.filepicker_parentdir);
			}

			@Override
			public int compareTo(@NonNull Object o) {
				if (!(o instanceof FileInfo)) return -1000;
				FileInfo another = (FileInfo) o;
				if (this.type == 0) {
					if (another.type == 1) return 1;
					if (another.type == 100) return 100;
					return deepCompare(this, another);
				}
				if (this.type == 1) {
					if (another.type == 0) return -1;
					if (another.type == 100) return 100;
					return deepCompare(this, another);
				}
				return -100;
			}

			private int deepCompare(final FileInfo f0, final FileInfo f1) {
				switch (sortby) {
					case 1: {
						long t0 = f0.file.lastModified();
						long t1 = f1.file.lastModified();
						if (t0 > t1) return -1;
						if (t0 < t1) return 1;
						return 0;
					}
					case 2: {
						long t0 = f0.file.lastModified();
						long t1 = f1.file.lastModified();
						if (t0 > t1) return 1;
						if (t0 < t1) return -1;
						return 0;
					}
					default:
						return f0.file.compareTo(f1.file);
				}
			}
		}
	}
}
