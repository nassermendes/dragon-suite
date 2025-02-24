import os
import winreg
import sys

def add_to_path(new_path):
    # Get the current PATH
    with winreg.OpenKey(winreg.HKEY_CURRENT_USER, "Environment", 0, winreg.KEY_ALL_ACCESS) as key:
        try:
            path_value, _ = winreg.QueryValueEx(key, "Path")
        except WindowsError:
            path_value = ""

    # Check if the path is already in PATH
    paths = path_value.split(";")
    if new_path not in paths:
        # Add the new path
        new_path_value = path_value + ";" + new_path if path_value else new_path
        
        # Update the PATH
        winreg.SetValueEx(key, "Path", 0, winreg.REG_EXPAND_SZ, new_path_value)
        print(f"Added {new_path} to PATH")
        return True
    else:
        print(f"{new_path} is already in PATH")
        return False

if __name__ == "__main__":
    flutter_path = os.path.join(os.path.expanduser("~"), "flutter", "bin")
    add_to_path(flutter_path)
    print("Please restart your terminal for the changes to take effect.")
