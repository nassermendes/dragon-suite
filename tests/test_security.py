import pytest
import os
from pathlib import Path
from scripts.ci_helpers import setup_ci_environment, validate_ci_config

class TestSecurity:
    @pytest.fixture
    def setup_env(self):
        # Setup test environment
        os.environ['TESTING'] = 'true'
        os.environ['CI'] = 'true'
        yield
        # Cleanup
        os.environ.pop('TESTING', None)
        os.environ.pop('CI', None)
    
    def test_ci_environment_setup(self, setup_env):
        result = setup_ci_environment()
        assert result is True
        assert Path('.github/workflows').exists()
        assert Path('.gitlab').exists()
    
    def test_ci_config_validation(self, setup_env):
        # Create test workflow files
        workflow_content = """
        name: CI
        on: [push]
        jobs:
          security:
            runs-on: ubuntu-latest
            steps:
              - uses: GitGuardian/ggshield-action@master
        """
        os.makedirs('.github/workflows', exist_ok=True)
        with open('.github/workflows/ci.yml', 'w') as f:
            f.write(workflow_content)
        
        result = validate_ci_config()
        assert result is True
