package com.cybertron.account.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.util.Log;

public class PackageUtils {
	private static final String TAG = PackageUtils.class.getSimpleName();

	private PackageUtils() {
	}
	
	public static List<ApplicationInfo> listApps(PackageManager pm) {
		return listApps(pm, 
				PackageManager.GET_UNINSTALLED_PACKAGES | 
				PackageManager.GET_DISABLED_COMPONENTS);
	}
	
	public static List<ApplicationInfo> listApps(PackageManager pm, int flags) {
		return pm.getInstalledApplications(flags);
	}
	
	public static void printAppInfos(PackageManager pm, List<ApplicationInfo> infos) {
		for (int i=0; i<infos.size(); i++) {
			ApplicationInfo info = infos.get(i);
			int state = pm.getApplicationEnabledSetting(info.packageName);
			boolean isEnable = toEnable(state);
			Log.i(TAG, "" + isEnable + ", " + info.loadLabel(pm) + ", "
					+ info.packageName);
		}
	}
	
    public static List<ResolveInfo> listBootReceivers(PackageManager pm) {
    	return listReceivers(pm, Intent.ACTION_BOOT_COMPLETED);
    }
    
    public static List<ResolveInfo> listReceivers(PackageManager pm, String action) {
    	Intent intent = new Intent(action);
		return pm.queryBroadcastReceivers(intent,
				PackageManager.GET_DISABLED_COMPONENTS);
    }
    
    public static void printBootStartReceivers(PackageManager pm) {
    	List<ResolveInfo> list = listBootReceivers(pm);
    	for (int i=0; i<list.size(); i++) {
    		ResolveInfo ri = list.get(i);
			ComponentName componentName = new ComponentName(
					ri.activityInfo.packageName, ri.activityInfo.name);
    		int state = pm.getComponentEnabledSetting(componentName);
    		boolean isBoot = isComponentEnable(pm, componentName);
			Log.i(TAG, "" + state + "," + isBoot + "," + ri.loadLabel(pm) + ","
					+ componentName.flattenToString());
    	}
    }

    // one package maybe more than one Component
    // Map<pkg, List[cls1, cls2, cls3]>
    public static Map<String, List<String>> arrangeByPackage(List<ResolveInfo> comps) {
    	HashMap<String, List<String>> map = new HashMap<String, List<String>>();
    	
    	ResolveInfo ri = null;
    	String pkg = null;
    	String cls = null;
    	List<String> classes = null;
    	
    	for (int i=0; i<comps.size(); i++) {
    		ri = comps.get(i);
    		pkg = ri.activityInfo.packageName;
    		cls = ri.activityInfo.name;
    		if (map.containsKey(pkg)) {
    			classes = map.get(pkg);
    			classes.add(cls);
    		} else {
    			classes = new ArrayList<String>();
    			classes.add(cls);
    			map.put(pkg, classes);
    		}
    	}
    	return map;
    }
    
    public static void printMap(Map<String, List<String>> map) {
    	int index = 0;
    	Set<Entry<String, List<String>>> set = map.entrySet();
    	for (Iterator<Entry<String, List<String>>> iterator = set.iterator(); 
    			iterator.hasNext(); index++) {
			Entry<String, List<String>> entry = iterator.next();
			Log.i(TAG, "["+index+"]"+ entry.getKey() + entry.getValue());
		}
    }
    
    public static boolean isAppEnable(PackageManager pm, String packageName) {
		int state = pm.getApplicationEnabledSetting(packageName);
		return toEnable(state);
    }
    
    public static void setAppEnable(PackageManager pm, String packageName, boolean enable) {
    	if (enable != isAppEnable(pm, packageName)) {
    		int newState = toState(enable);
			pm.setApplicationEnabledSetting(packageName, newState,
					PackageManager.DONT_KILL_APP);
    	}
    }
    
	public static boolean isSystemApp(ApplicationInfo info) {
		if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0
			|| (info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			return true;
		}
		return false;
	}

	public static boolean isSystemApp(PackageManager pm, String packageName)
			throws NameNotFoundException {
		ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
		return isSystemApp(info);
	}

	public static boolean isComponentEnable(PackageManager pm,
			ComponentName componentName) {
		int state = pm.getComponentEnabledSetting(componentName);
		return toEnable(state);
	}

	public static boolean isComponentEnable(PackageManager pm, String packageName,
			String cls) {
		ComponentName componentName = new ComponentName(packageName, cls);
		return isComponentEnable(pm, componentName);
	}
	
	public static void setComponentEnable(PackageManager pm,
			ComponentName componentName, boolean enable) {
		if (isComponentEnable(pm, componentName) != enable) {
			int newState = toState(enable);
			pm.setComponentEnabledSetting(componentName, newState,
					PackageManager.DONT_KILL_APP);
		}
	}

	public static void setComponentEnable(PackageManager pm, String packageName,
			String cls, boolean enable) {
		ComponentName componentName = new ComponentName(packageName, cls);
		setComponentEnable(pm, componentName, enable);
	}
	
	public static int toState(boolean enable) {
		return enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED 
	                  : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
	}
	
	public static boolean toEnable(int state) {
		return ((state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) 
		     || (state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED));
	}
}
