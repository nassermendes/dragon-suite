from pathlib import Path
import sys
import os

class TypeWrightConfig:
    ROOT_DIR = Path(__file__).parent.parent
    TEST_DIR = ROOT_DIR / 'tests'
    ANDROID_DIR = ROOT_DIR / 'android'
    SCRIPTS_DIR = ROOT_DIR / 'scripts'
    
    EXCLUDED_PATHS = [
        '**/build/**',
        '**/dist/**',
        '**/node_modules/**',
        '**/.git/**',
        '**/__pycache__/**',
        '**/*.pyc',
        '**/venv/**'
    ]
    
    TEST_PATTERNS = [
        '**/*_test.py',
        '**/*_test.kt',
        '**/test_*.py',
        '**/Test*.kt'
    ]
    
    @classmethod
    def setup_test_env(cls):
        """Setup test environment"""
        sys.path.insert(0, str(cls.ROOT_DIR))
        os.environ['TESTING'] = 'true'
        os.environ['TYPEWRIGHT_MODE'] = 'true'
    
    @classmethod
    def get_test_files(cls):
        """Get all test files"""
        test_files = []
        for pattern in cls.TEST_PATTERNS:
            test_files.extend(cls.ROOT_DIR.glob(pattern))
        return test_files

    @classmethod
    def is_excluded(cls, path):
        """Check if path should be excluded"""
        from fnmatch import fnmatch
        path_str = str(path)
        return any(fnmatch(path_str, pattern) for pattern in cls.EXCLUDED_PATHS)
