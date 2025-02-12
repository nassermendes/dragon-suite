import os
import sys
import pytest
import logging
from pathlib import Path
from typing import List, Optional
from typewright_config import TypeWrightConfig

class TestRunner:
    def __init__(self):
        self.config = TypeWrightConfig
        self.config.setup_test_env()
        self.logger = self._setup_logger()
        
    def _setup_logger(self):
        logger = logging.getLogger('TypeWright')
        logger.setLevel(logging.INFO)
        handler = logging.StreamHandler(sys.stdout)
        handler.setFormatter(logging.Formatter(
            '%(asctime)s - %(name)s - %(levelname)s - %(message)s'
        ))
        logger.addHandler(handler)
        return logger
    
    def run_tests(self, patterns: Optional[List[str]] = None):
        """Run tests matching the given patterns"""
        self.logger.info("Starting TypeWright test runner...")
        
        if patterns:
            test_files = []
            for pattern in patterns:
                test_files.extend(Path().glob(pattern))
        else:
            test_files = self.config.get_test_files()
        
        test_files = [str(f) for f in test_files if not self.config.is_excluded(f)]
        
        if not test_files:
            self.logger.warning("No test files found!")
            return False
        
        self.logger.info(f"Found {len(test_files)} test files")
        
        try:
            exit_code = pytest.main([
                '-v',
                '--cov=.',
                '--cov-report=xml',
                '--cov-report=term',
                *test_files
            ])
            
            success = exit_code == 0
            status = "PASSED" if success else "FAILED"
            self.logger.info(f"Test suite {status}")
            return success
            
        except Exception as e:
            self.logger.error(f"Error running tests: {e}")
            return False

if __name__ == '__main__':
    runner = TestRunner()
    patterns = sys.argv[1:] if len(sys.argv) > 1 else None
    success = runner.run_tests(patterns)
    sys.exit(0 if success else 1)
