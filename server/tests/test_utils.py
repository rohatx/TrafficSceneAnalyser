# tests/test_utils.py
import unittest
from unittest.mock import patch, Mock
import sys
import os

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '../')))

from api.utils import query_bridges_nearby, check_way, query_tunnels_nearby, calculate_probability

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

    @patch('api.utils.overpy.Overpass.query')
    def test_query_tunnels_nearby_positive(self, mock_query):
        mock_result = Mock()
        mock_result.ways = ['tunnel1']
        mock_query.return_value = mock_result
        result = query_tunnels_nearby(37.81713298488588, -122.47837630605842)
        self.assertTrue(result)

    @patch('api.utils.overpy.Overpass.query')
    def test_query_tunnels_nearby_negative(self, mock_query):
        mock_result = Mock()
        mock_result.ways = []
        mock_query.return_value = mock_result
        result = query_tunnels_nearby(37.81713298488588, -122.47837630605842)
        self.assertFalse(result)

    def test_calculate_probability(self):
        C_LLM = 0.9
        road_detected = True
        I_QS = 0.8
        H_ACC = 0.85

        # Image Quality Adjustment
        IQS_Adjustment = round((1 - I_QS) * 0.05, 2)
        
        # Historical Accuracy Adjustment
        HA_Adjustment = round(1 - H_ACC, 2)
        
        # Total Adjustment
        Adjustment = round(IQS_Adjustment + HA_Adjustment, 2)
        
        # Final Probability
        expected_probability = round(C_LLM * (1 - Adjustment), 2)
        
        result, details = calculate_probability(C_LLM, road_detected, I_QS, H_ACC)
        self.assertEqual(result, expected_probability)
        self.assertEqual(details["formula"], "P_final = C_LLM * road_adjustment * (1 - (IQS_Adjustment + HA_Adjustment))")
        self.assertEqual(details["calculation"], f"P_final = {C_LLM} * 1 * (1 - ({IQS_Adjustment} + {HA_Adjustment}))")

    def test_calculate_probability_no_road(self):
        C_LLM = 0.9
        road_detected = False
        I_QS = 0.8
        H_ACC = 0.85

        # Image Quality Adjustment
        IQS_Adjustment = round((1 - I_QS) * 0.05, 2)
        
        # Historical Accuracy Adjustment
        HA_Adjustment = round(1 - H_ACC, 2)
        
        # Total Adjustment
        Adjustment = round(IQS_Adjustment + HA_Adjustment, 2)
        
        # Final Probability with road detection penalty
        expected_probability = round(C_LLM * 0.5 * (1 - Adjustment), 2)
        
        result, details = calculate_probability(C_LLM, road_detected, I_QS, H_ACC)
        self.assertEqual(result, expected_probability)
        self.assertEqual(details["formula"], "P_final = C_LLM * road_adjustment * (1 - (IQS_Adjustment + HA_Adjustment))")
        self.assertEqual(details["calculation"], f"P_final = {C_LLM } * 0.5 * (1 - ({IQS_Adjustment} + {HA_Adjustment}))")

if __name__ == '__main__':
    unittest.main()
