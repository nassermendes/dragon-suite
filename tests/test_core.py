"""
Tests for Dragon Suite core functionality
"""

import pytest
from src.core import DragonSuite

def test_dragon_suite_initialization():
    """Test basic initialization of DragonSuite"""
    dragon = DragonSuite()
    assert dragon is not None
    
def test_dragon_suite_status():
    """Test status reporting"""
    dragon = DragonSuite()
    status = dragon.get_status()
    assert isinstance(status, dict)
    assert status["status"] == "running"
    assert status["config_loaded"] is False
