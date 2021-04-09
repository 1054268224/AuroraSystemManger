// Gionee: <houjie> <2015-10-27> add for CR01575153 begin 
package com.cydroid.softmanager.rubbishcleaner.interfaces;

public interface RubbishCleanerScanListener {
    void onScanStart();
    
    void onScanItem(Object obj);
    void onFindItem(Object obj);
    void onScanEnd(int group);
}
// Gionee: <houjie> <2015-10-27> add for CR01575153 end