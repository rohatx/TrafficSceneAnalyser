# tests/test_utils.py
import unittest
from unittest.mock import patch, Mock
import sys
import os

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '../')))

from api.utils import query_bridges_nearby, check_way, calculate_probability

# Weighting of variables
w_CLLM = 0.4
w_BSC = 0.2
w_RSC = 0.1
w_IQS = 0.2
w_HACC = 0.1

class TestImageProcessing(unittest.TestCase):
    @patch('api.utils.overpy.Overpass.query')
    def test_query_bridges_nearby_positive(self, mock_query):
        mock_result = Mock()
        mock_result.ways = ['bridge1']
        mock_query.return_value = mock_result
        result = query_bridges_nearby(37.81713298488588, -122.47837630605842)
        self.assertTrue(result)

    @patch('api.utils.overpy.Overpass.query')
    def test_query_bridges_nearby_negative(self, mock_query):
        mock_result = Mock()
        mock_result.ways = []
        mock_query.return_value = mock_result
        result = query_bridges_nearby(37.81713298488588, -122.47837630605842)
        self.assertFalse(result)

    @patch('api.utils.overpy.Overpass.query')
    def test_check_way_positive(self, mock_query):
        mock_result = Mock()
        mock_result.ways = ['way1']
        mock_query.return_value = mock_result
        result = check_way(37.81713298488588, -122.47837630605842)
        self.assertTrue(result)

    @patch('api.utils.overpy.Overpass.query')
    def test_check_way_negative(self, mock_query):
        mock_result = Mock()
        mock_result.ways = []
        mock_query.return_value = mock_result
        result = check_way(37.81713298488588, -122.47837630605842)
        self.assertFalse(result)

    def test_calculate_probability(self):
        C_LLM = 0.9
        bridge_detected = True
        road_detected = True
        I_QS = 0.8
        H_ACC = 0.85
        expected_probability = round(w_CLLM * C_LLM + w_BSC * 1 + w_RSC * 1 + w_IQS * I_QS + w_HACC * H_ACC, 2)
        result = calculate_probability(C_LLM, bridge_detected, road_detected, I_QS, H_ACC)
        self.assertEqual(result, expected_probability)

if __name__ == '__main__':
    unittest.main()
