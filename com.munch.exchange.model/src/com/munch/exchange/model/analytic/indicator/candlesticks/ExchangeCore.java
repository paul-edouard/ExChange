package com.munch.exchange.model.analytic.indicator.candlesticks;

import com.tictactec.ta.lib.CandleSettingType;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RangeType;
import com.tictactec.ta.lib.RetCode;

public class ExchangeCore extends Core {
	
	
	
	private boolean isBodyLong(int i, double BodyLongPeriodTotal, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose){
		return	( Math.abs ( inClose[i] - inOpen[i] ) ) > ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].avgPeriod) != 0.0? BodyLongPeriodTotal / (this.candleSettings[CandleSettingType.BodyLong.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) );
	}
	
	private boolean isBodyDoji(int i, double BodyDojiPeriodTotal, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose){
		return	 (Math.abs ( inClose[i] - inOpen[i] ) ) <= ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].avgPeriod) != 0.0? BodyDojiPeriodTotal / (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) );
	}
	
	private boolean isBodyShort(int i, double BodyShortPeriodTotal, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose){
		return	( Math.abs ( inClose[i] - inOpen[i] ) ) > ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].avgPeriod) != 0.0? BodyShortPeriodTotal / (this.candleSettings[CandleSettingType.BodyShort.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) ) ;
	}
	
	private double calculateBodySize(int i, CandleSettingType type, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose){
		return ( (this.candleSettings[type.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[type.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[type.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ;
	}
	
	

	@Override
	public RetCode cdlAbandonedBaby(int startIdx, int endIdx, double[] inOpen, double[] inHigh, double[] inLow,
			double[] inClose, double optInPenetration, MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		double BodyDojiPeriodTotal, BodyLongPeriodTotal, BodyShortPeriodTotal;
	      int i, outIdx, BodyDojiTrailingIdx, BodyLongTrailingIdx, BodyShortTrailingIdx, lookbackTotal;
	      if( startIdx < 0 )
	         return RetCode.OutOfRangeStartIndex ;
	      if( (endIdx < 0) || (endIdx < startIdx))
	         return RetCode.OutOfRangeEndIndex ;
	      if( optInPenetration == (-4e+37) )
	         optInPenetration = 3.000000e-1;
	      else if( (optInPenetration < 0.000000e+0) || (optInPenetration > 3.000000e+37) )
	         return RetCode.BadParam ;
	      lookbackTotal = cdlAbandonedBabyLookback (optInPenetration);
	      if( startIdx < lookbackTotal )
	         startIdx = lookbackTotal;
	      if( startIdx > endIdx )
	      {
	         outBegIdx.value = 0 ;
	         outNBElement.value = 0 ;
	         return RetCode.Success ;
	      }
	      BodyLongPeriodTotal = 0;
	      BodyDojiPeriodTotal = 0;
	      BodyShortPeriodTotal = 0;
	      BodyLongTrailingIdx = startIdx -2 - (this.candleSettings[CandleSettingType.BodyLong.ordinal()].avgPeriod) ;
	      BodyDojiTrailingIdx = startIdx -1 - (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].avgPeriod) ;
	      BodyShortTrailingIdx = startIdx - (this.candleSettings[CandleSettingType.BodyShort.ordinal()].avgPeriod) ;
	      i = BodyLongTrailingIdx;
	      while( i < startIdx-2 ) {
	         BodyLongPeriodTotal +=calculateBodySize(i, CandleSettingType.BodyLong, inOpen, inHigh, inLow, inClose);
	         i++;
	      }
	      i = BodyDojiTrailingIdx;
	      while( i < startIdx-1 ) {
	         BodyDojiPeriodTotal += calculateBodySize(i, CandleSettingType.BodyDoji, inOpen, inHigh, inLow, inClose);
	         i++;
	      }
	      i = BodyShortTrailingIdx;
	      while( i < startIdx ) {
	         BodyShortPeriodTotal += calculateBodySize(i, CandleSettingType.BodyShort, inOpen, inHigh, inLow, inClose);
	         i++;
	      }
	      i = startIdx;
	      outIdx = 0;
	      do
	      {
	    	 outInteger[outIdx] = 0;
	         
	    	 if(	isBodyLong(i-2, BodyLongPeriodTotal, inOpen, inHigh, inLow, inClose) &&
	        		isBodyDoji(i-1, BodyDojiPeriodTotal, inOpen, inHigh, inLow, inClose) &&
	        		isBodyShort(i, BodyShortPeriodTotal, inOpen, inHigh, inLow, inClose) ){
	        	 
	        	 if( 	inClose[i-2] >= inOpen[i-2] &&
	            		inClose[i] <= inOpen[i]  &&
	            		inClose[i] < inClose[i-2] - ( Math.abs ( inClose[i-2] - inOpen[i-2] ) ) * optInPenetration &&
	            		inLow[i-1] >= inHigh[i-2] - ( Math.abs ( inClose[i-2] - inOpen[i-2] ) ) * optInPenetration&&
	            		inHigh[i] <= inLow[i-1]  + ( Math.abs ( inClose[i] - inOpen[i] ) ) * optInPenetration){
	        		 
	        		 outInteger[outIdx] = - 100;
	        	 	}
	        	 
	        	 if(
	            		inClose[i-2] <= inOpen[i-2] &&
	            		inClose[i] >= inOpen[i]  && 
	            		inClose[i] > inClose[i-2] + ( Math.abs ( inClose[i-2] - inOpen[i-2] ) ) * optInPenetration &&
	            		inHigh[i-1] <= inLow[i-2] + ( Math.abs ( inClose[i-2] - inOpen[i-2] ) ) * optInPenetration &&
		            	inLow[i] >= inHigh[i-1] - ( Math.abs ( inClose[i] - inOpen[i] ) ) * optInPenetration){
	        			 outInteger[outIdx] = 100;
	        		 }
	        	 
	         }
	         
	         
	         outIdx++;
	         
	         BodyLongPeriodTotal += 	calculateBodySize(i-2, CandleSettingType.BodyLong, inOpen, inHigh, inLow, inClose)
	        		 					- calculateBodySize(BodyLongTrailingIdx, CandleSettingType.BodyLong, inOpen, inHigh, inLow, inClose);
	         BodyDojiPeriodTotal += 	calculateBodySize(i-1, CandleSettingType.BodyDoji, inOpen, inHigh, inLow, inClose)
	        		 					- calculateBodySize(BodyDojiTrailingIdx, CandleSettingType.BodyDoji, inOpen, inHigh, inLow, inClose);
	         BodyShortPeriodTotal += 	calculateBodySize(i, CandleSettingType.BodyShort, inOpen, inHigh, inLow, inClose)
	        		 					- calculateBodySize(BodyShortTrailingIdx, CandleSettingType.BodyShort, inOpen, inHigh, inLow, inClose);
	         
	         i++;
	         BodyLongTrailingIdx++;
	         BodyDojiTrailingIdx++;
	         BodyShortTrailingIdx++;
	         
	      } while( i <= endIdx );
	      outNBElement.value = outIdx;
	      outBegIdx.value = startIdx;
	      
//	      System.out.println("Success!");
	      
	      return RetCode.Success ;
	}
	
	

	@Override
	public RetCode cdlBreakaway(int startIdx, int endIdx, double[] inOpen, double[] inHigh, double[] inLow,
			double[] inClose, MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		double BodyLongPeriodTotal;
	      int i, outIdx, BodyLongTrailingIdx, lookbackTotal;
	      if( startIdx < 0 )
	         return RetCode.OutOfRangeStartIndex ;
	      if( (endIdx < 0) || (endIdx < startIdx))
	         return RetCode.OutOfRangeEndIndex ;
	      lookbackTotal = cdlBreakawayLookback ();
	      if( startIdx < lookbackTotal )
	         startIdx = lookbackTotal;
	      if( startIdx > endIdx )
	      {
	         outBegIdx.value = 0 ;
	         outNBElement.value = 0 ;
	         return RetCode.Success ;
	      }
	      BodyLongPeriodTotal = 0;
	      BodyLongTrailingIdx = startIdx - (this.candleSettings[CandleSettingType.BodyLong.ordinal()].avgPeriod) ;
	      i = BodyLongTrailingIdx;
	      while( i < startIdx ) {
	         BodyLongPeriodTotal += ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-4] - inOpen[i-4] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-4] - inLow[i-4] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-4] - ( inClose[i-4] >= inOpen[i-4] ? inClose[i-4] : inOpen[i-4] ) ) + ( ( inClose[i-4] >= inOpen[i-4] ? inOpen[i-4] : inClose[i-4] ) - inLow[i-4] ) : 0 ) ) ) ;
	         i++;
	      }
	      i = startIdx;
	      outIdx = 0;
	      do
	      {
	         if( ( Math.abs ( inClose[i-4] - inOpen[i-4] ) ) > ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].avgPeriod) != 0.0? BodyLongPeriodTotal / (this.candleSettings[CandleSettingType.BodyLong.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-4] - inOpen[i-4] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-4] - inLow[i-4] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-4] - ( inClose[i-4] >= inOpen[i-4] ? inClose[i-4] : inOpen[i-4] ) ) + ( ( inClose[i-4] >= inOpen[i-4] ? inOpen[i-4] : inClose[i-4] ) - inLow[i-4] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) ) &&
	            ( inClose[i-4] >= inOpen[i-4] ? 1 : -1 ) == ( inClose[i-3] >= inOpen[i-3] ? 1 : -1 ) &&
	            ( inClose[i-3] >= inOpen[i-3] ? 1 : -1 ) == ( inClose[i-1] >= inOpen[i-1] ? 1 : -1 ) &&
	            ( inClose[i-1] >= inOpen[i-1] ? 1 : -1 ) == - ( inClose[i] >= inOpen[i] ? 1 : -1 ) &&
	            (
	            ( ( inClose[i-4] >= inOpen[i-4] ? 1 : -1 ) == -1 &&
	            ( (((inOpen[i-3]) > (inClose[i-3])) ? (inOpen[i-3]) : (inClose[i-3])) <= (((inOpen[i-4]) < (inClose[i-4])) ? (inOpen[i-4]) : (inClose[i-4])) ) &&
	            inHigh[i-2] < inHigh[i-3] && inLow[i-2] < inLow[i-3] &&
	            inHigh[i-1] < inHigh[i-2] && inLow[i-1] < inLow[i-2] &&
	            inClose[i] > inOpen[i-3] && inClose[i] < inClose[i-4]
	            )
	            ||
	            ( ( inClose[i-4] >= inOpen[i-4] ? 1 : -1 ) == 1 &&
	            ( (((inOpen[i-3]) < (inClose[i-3])) ? (inOpen[i-3]) : (inClose[i-3])) >= (((inOpen[i-4]) > (inClose[i-4])) ? (inOpen[i-4]) : (inClose[i-4])) ) &&
	            inHigh[i-2] > inHigh[i-3] && inLow[i-2] > inLow[i-3] &&
	            inHigh[i-1] > inHigh[i-2] && inLow[i-1] > inLow[i-2] &&
	            inClose[i] < inOpen[i-3] && inClose[i] > inClose[i-4]
	            )
	            )
	            )
	            outInteger[outIdx++] = ( inClose[i] >= inOpen[i] ? 1 : -1 ) * 100;
	         else
	            outInteger[outIdx++] = 0;
	         BodyLongPeriodTotal += ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-4] - inOpen[i-4] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-4] - inLow[i-4] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-4] - ( inClose[i-4] >= inOpen[i-4] ? inClose[i-4] : inOpen[i-4] ) ) + ( ( inClose[i-4] >= inOpen[i-4] ? inOpen[i-4] : inClose[i-4] ) - inLow[i-4] ) : 0 ) ) )
	            - ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[BodyLongTrailingIdx-4] - inOpen[BodyLongTrailingIdx-4] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[BodyLongTrailingIdx-4] - inLow[BodyLongTrailingIdx-4] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[BodyLongTrailingIdx-4] - ( inClose[BodyLongTrailingIdx-4] >= inOpen[BodyLongTrailingIdx-4] ? inClose[BodyLongTrailingIdx-4] : inOpen[BodyLongTrailingIdx-4] ) ) + ( ( inClose[BodyLongTrailingIdx-4] >= inOpen[BodyLongTrailingIdx-4] ? inOpen[BodyLongTrailingIdx-4] : inClose[BodyLongTrailingIdx-4] ) - inLow[BodyLongTrailingIdx-4] ) : 0 ) ) ) ;
	         i++;
	         BodyLongTrailingIdx++;
	      } while( i <= endIdx );
	      outNBElement.value = outIdx;
	      outBegIdx.value = startIdx;
	      return RetCode.Success ;
	}
	

	@Override
	public RetCode cdlConcealBabysWall(int startIdx, int endIdx, double[] inOpen, double[] inHigh, double[] inLow,
			double[] inClose, MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		 double []ShadowVeryShortPeriodTotal = new double[4] ;
	      int i, outIdx, totIdx, ShadowVeryShortTrailingIdx, lookbackTotal;
	      if( startIdx < 0 )
	         return RetCode.OutOfRangeStartIndex ;
	      if( (endIdx < 0) || (endIdx < startIdx))
	         return RetCode.OutOfRangeEndIndex ;
	      lookbackTotal = cdlConcealBabysWallLookback ();
	      if( startIdx < lookbackTotal )
	         startIdx = lookbackTotal;
	      if( startIdx > endIdx )
	      {
	         outBegIdx.value = 0 ;
	         outNBElement.value = 0 ;
	         return RetCode.Success ;
	      }
	      ShadowVeryShortPeriodTotal[3] = 0;
	      ShadowVeryShortPeriodTotal[2] = 0;
	      ShadowVeryShortPeriodTotal[1] = 0;
	      ShadowVeryShortTrailingIdx = startIdx - (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].avgPeriod) ;
	      i = ShadowVeryShortTrailingIdx;
	      while( i < startIdx ) {
	         ShadowVeryShortPeriodTotal[3] += ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-3] - inOpen[i-3] ) ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-3] - inLow[i-3] ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-3] - ( inClose[i-3] >= inOpen[i-3] ? inClose[i-3] : inOpen[i-3] ) ) + ( ( inClose[i-3] >= inOpen[i-3] ? inOpen[i-3] : inClose[i-3] ) - inLow[i-3] ) : 0 ) ) ) ;
	         ShadowVeryShortPeriodTotal[2] += ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-2] - inOpen[i-2] ) ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-2] - inLow[i-2] ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-2] - ( inClose[i-2] >= inOpen[i-2] ? inClose[i-2] : inOpen[i-2] ) ) + ( ( inClose[i-2] >= inOpen[i-2] ? inOpen[i-2] : inClose[i-2] ) - inLow[i-2] ) : 0 ) ) ) ;
	         ShadowVeryShortPeriodTotal[1] += ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-1] - inOpen[i-1] ) ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-1] - inLow[i-1] ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-1] - ( inClose[i-1] >= inOpen[i-1] ? inClose[i-1] : inOpen[i-1] ) ) + ( ( inClose[i-1] >= inOpen[i-1] ? inOpen[i-1] : inClose[i-1] ) - inLow[i-1] ) : 0 ) ) ) ;
	         i++;
	      }
	      i = startIdx;
	      outIdx = 0;
	      do
	      {
	         if( ( inClose[i-3] >= inOpen[i-3] ? 1 : -1 ) == -1 &&
	            ( inClose[i-2] >= inOpen[i-2] ? 1 : -1 ) == -1 &&
	            ( inClose[i-1] >= inOpen[i-1] ? 1 : -1 ) == -1 &&
	            ( inClose[i] >= inOpen[i] ? 1 : -1 ) == -1 &&
	            ( ( inClose[i-3] >= inOpen[i-3] ? inOpen[i-3] : inClose[i-3] ) - inLow[i-3] ) < ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].avgPeriod) != 0.0? ShadowVeryShortPeriodTotal[3] / (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-3] - inOpen[i-3] ) ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-3] - inLow[i-3] ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-3] - ( inClose[i-3] >= inOpen[i-3] ? inClose[i-3] : inOpen[i-3] ) ) + ( ( inClose[i-3] >= inOpen[i-3] ? inOpen[i-3] : inClose[i-3] ) - inLow[i-3] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) ) &&
	            ( inHigh[i-3] - ( inClose[i-3] >= inOpen[i-3] ? inClose[i-3] : inOpen[i-3] ) ) < ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].avgPeriod) != 0.0? ShadowVeryShortPeriodTotal[3] / (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-3] - inOpen[i-3] ) ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-3] - inLow[i-3] ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-3] - ( inClose[i-3] >= inOpen[i-3] ? inClose[i-3] : inOpen[i-3] ) ) + ( ( inClose[i-3] >= inOpen[i-3] ? inOpen[i-3] : inClose[i-3] ) - inLow[i-3] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) ) &&
	            ( ( inClose[i-2] >= inOpen[i-2] ? inOpen[i-2] : inClose[i-2] ) - inLow[i-2] ) < ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].avgPeriod) != 0.0? ShadowVeryShortPeriodTotal[2] / (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-2] - inOpen[i-2] ) ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-2] - inLow[i-2] ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-2] - ( inClose[i-2] >= inOpen[i-2] ? inClose[i-2] : inOpen[i-2] ) ) + ( ( inClose[i-2] >= inOpen[i-2] ? inOpen[i-2] : inClose[i-2] ) - inLow[i-2] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) ) &&
	            ( inHigh[i-2] - ( inClose[i-2] >= inOpen[i-2] ? inClose[i-2] : inOpen[i-2] ) ) < ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].avgPeriod) != 0.0? ShadowVeryShortPeriodTotal[2] / (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-2] - inOpen[i-2] ) ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-2] - inLow[i-2] ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-2] - ( inClose[i-2] >= inOpen[i-2] ? inClose[i-2] : inOpen[i-2] ) ) + ( ( inClose[i-2] >= inOpen[i-2] ? inOpen[i-2] : inClose[i-2] ) - inLow[i-2] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) ) &&
	            ( (((inOpen[i-1]) > (inClose[i-1])) ? (inOpen[i-1]) : (inClose[i-1])) <= (((inOpen[i-2]) < (inClose[i-2])) ? (inOpen[i-2]) : (inClose[i-2])) ) &&
	            ( inHigh[i-1] - ( inClose[i-1] >= inOpen[i-1] ? inClose[i-1] : inOpen[i-1] ) ) > ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].avgPeriod) != 0.0? ShadowVeryShortPeriodTotal[1] / (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-1] - inOpen[i-1] ) ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-1] - inLow[i-1] ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-1] - ( inClose[i-1] >= inOpen[i-1] ? inClose[i-1] : inOpen[i-1] ) ) + ( ( inClose[i-1] >= inOpen[i-1] ? inOpen[i-1] : inClose[i-1] ) - inLow[i-1] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) ) &&
	            inHigh[i-1] > inClose[i-2] &&
	            inHigh[i] > inHigh[i-1] && inLow[i] < inLow[i-1]
	            )
	            outInteger[outIdx++] = 100;
	         else
	            outInteger[outIdx++] = 0;
	         for (totIdx = 3; totIdx >= 1; --totIdx)
	            ShadowVeryShortPeriodTotal[totIdx] += ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-totIdx] - inOpen[i-totIdx] ) ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-totIdx] - inLow[i-totIdx] ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-totIdx] - ( inClose[i-totIdx] >= inOpen[i-totIdx] ? inClose[i-totIdx] : inOpen[i-totIdx] ) ) + ( ( inClose[i-totIdx] >= inOpen[i-totIdx] ? inOpen[i-totIdx] : inClose[i-totIdx] ) - inLow[i-totIdx] ) : 0 ) ) )
	            - ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[ShadowVeryShortTrailingIdx-totIdx] - inOpen[ShadowVeryShortTrailingIdx-totIdx] ) ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[ShadowVeryShortTrailingIdx-totIdx] - inLow[ShadowVeryShortTrailingIdx-totIdx] ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[ShadowVeryShortTrailingIdx-totIdx] - ( inClose[ShadowVeryShortTrailingIdx-totIdx] >= inOpen[ShadowVeryShortTrailingIdx-totIdx] ? inClose[ShadowVeryShortTrailingIdx-totIdx] : inOpen[ShadowVeryShortTrailingIdx-totIdx] ) ) + ( ( inClose[ShadowVeryShortTrailingIdx-totIdx] >= inOpen[ShadowVeryShortTrailingIdx-totIdx] ? inOpen[ShadowVeryShortTrailingIdx-totIdx] : inClose[ShadowVeryShortTrailingIdx-totIdx] ) - inLow[ShadowVeryShortTrailingIdx-totIdx] ) : 0 ) ) ) ;
	         i++;
	         ShadowVeryShortTrailingIdx++;
	      } while( i <= endIdx );
	      outNBElement.value = outIdx;
	      outBegIdx.value = startIdx;
	      return RetCode.Success ;
	}

	@Override
	public RetCode cdlDojiStar(int startIdx, int endIdx, double[] inOpen, double[] inHigh, double[] inLow,
			double[] inClose, MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		double BodyDojiPeriodTotal, BodyLongPeriodTotal;
	      int i, outIdx, BodyDojiTrailingIdx, BodyLongTrailingIdx, lookbackTotal;
	      if( startIdx < 0 )
	         return RetCode.OutOfRangeStartIndex ;
	      if( (endIdx < 0) || (endIdx < startIdx))
	         return RetCode.OutOfRangeEndIndex ;
	      lookbackTotal = cdlDojiStarLookback ();
	      if( startIdx < lookbackTotal )
	         startIdx = lookbackTotal;
	      if( startIdx > endIdx )
	      {
	         outBegIdx.value = 0 ;
	         outNBElement.value = 0 ;
	         return RetCode.Success ;
	      }
	      BodyLongPeriodTotal = 0;
	      BodyDojiPeriodTotal = 0;
	      BodyLongTrailingIdx = startIdx -1 - (this.candleSettings[CandleSettingType.BodyLong.ordinal()].avgPeriod) ;
	      BodyDojiTrailingIdx = startIdx - (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].avgPeriod) ;
	      i = BodyLongTrailingIdx;
	      while( i < startIdx-1 ) {
	         BodyLongPeriodTotal += ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ;
	         i++;
	      }
	      i = BodyDojiTrailingIdx;
	      while( i < startIdx ) {
	         BodyDojiPeriodTotal += ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ;
	         i++;
	      }
	      outIdx = 0;
	      do
	      {
	         if( ( Math.abs ( inClose[i-1] - inOpen[i-1] ) ) > ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].avgPeriod) != 0.0? BodyLongPeriodTotal / (this.candleSettings[CandleSettingType.BodyLong.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-1] - inOpen[i-1] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-1] - inLow[i-1] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-1] - ( inClose[i-1] >= inOpen[i-1] ? inClose[i-1] : inOpen[i-1] ) ) + ( ( inClose[i-1] >= inOpen[i-1] ? inOpen[i-1] : inClose[i-1] ) - inLow[i-1] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) ) &&
	            ( Math.abs ( inClose[i] - inOpen[i] ) ) <= ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].avgPeriod) != 0.0? BodyDojiPeriodTotal / (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) ) &&
	            ( ( ( inClose[i-1] >= inOpen[i-1] ? 1 : -1 ) == 1 && ( (((inOpen[i]) < (inClose[i])) ? (inOpen[i]) : (inClose[i])) >= (((inOpen[i-1]) > (inClose[i-1])) ? (inOpen[i-1]) : (inClose[i-1])) ) )
	            ||
	            ( ( inClose[i-1] >= inOpen[i-1] ? 1 : -1 ) == -1 && ( (((inOpen[i]) > (inClose[i])) ? (inOpen[i]) : (inClose[i])) <= (((inOpen[i-1]) < (inClose[i-1])) ? (inOpen[i-1]) : (inClose[i-1])) ) )
	            ) )
	            outInteger[outIdx++] = - ( inClose[i-1] >= inOpen[i-1] ? 1 : -1 ) * 100;
	         else
	            outInteger[outIdx++] = 0;
	         BodyLongPeriodTotal += ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-1] - inOpen[i-1] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-1] - inLow[i-1] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-1] - ( inClose[i-1] >= inOpen[i-1] ? inClose[i-1] : inOpen[i-1] ) ) + ( ( inClose[i-1] >= inOpen[i-1] ? inOpen[i-1] : inClose[i-1] ) - inLow[i-1] ) : 0 ) ) ) - ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[BodyLongTrailingIdx] - ( inClose[BodyLongTrailingIdx] >= inOpen[BodyLongTrailingIdx] ? inClose[BodyLongTrailingIdx] : inOpen[BodyLongTrailingIdx] ) ) + ( ( inClose[BodyLongTrailingIdx] >= inOpen[BodyLongTrailingIdx] ? inOpen[BodyLongTrailingIdx] : inClose[BodyLongTrailingIdx] ) - inLow[BodyLongTrailingIdx] ) : 0 ) ) ) ;
	         BodyDojiPeriodTotal += ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) - ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[BodyDojiTrailingIdx] - inOpen[BodyDojiTrailingIdx] ) ) : ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[BodyDojiTrailingIdx] - inLow[BodyDojiTrailingIdx] ) : ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[BodyDojiTrailingIdx] - ( inClose[BodyDojiTrailingIdx] >= inOpen[BodyDojiTrailingIdx] ? inClose[BodyDojiTrailingIdx] : inOpen[BodyDojiTrailingIdx] ) ) + ( ( inClose[BodyDojiTrailingIdx] >= inOpen[BodyDojiTrailingIdx] ? inOpen[BodyDojiTrailingIdx] : inClose[BodyDojiTrailingIdx] ) - inLow[BodyDojiTrailingIdx] ) : 0 ) ) ) ;
	         i++;
	         BodyLongTrailingIdx++;
	         BodyDojiTrailingIdx++;
	      } while( i <= endIdx );
	      outNBElement.value = outIdx;
	      outBegIdx.value = startIdx;
	      return RetCode.Success ;
	}

	@Override
	public RetCode cdlEngulfing(int startIdx, int endIdx, double[] inOpen, double[] inHigh, double[] inLow,
			double[] inClose, MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		int i, outIdx, lookbackTotal;
	      if( startIdx < 0 )
	         return RetCode.OutOfRangeStartIndex ;
	      if( (endIdx < 0) || (endIdx < startIdx))
	         return RetCode.OutOfRangeEndIndex ;
	      lookbackTotal = cdlEngulfingLookback ();
	      if( startIdx < lookbackTotal )
	         startIdx = lookbackTotal;
	      if( startIdx > endIdx )
	      {
	         outBegIdx.value = 0 ;
	         outNBElement.value = 0 ;
	         return RetCode.Success ;
	      }
	      i = startIdx;
	      outIdx = 0;
	      do
	      {
	         if( ( ( inClose[i] >= inOpen[i] ? 1 : -1 ) == 1 && ( inClose[i-1] >= inOpen[i-1] ? 1 : -1 ) == -1 &&
	            inClose[i] >= inOpen[i-1] && inOpen[i] <= inClose[i-1]
	            )
	            ||
	            ( ( inClose[i] >= inOpen[i] ? 1 : -1 ) == -1 && ( inClose[i-1] >= inOpen[i-1] ? 1 : -1 ) == 1 &&
	            inOpen[i] >= inClose[i-1] && inClose[i] <= inOpen[i-1]
	            )
	            )
	            outInteger[outIdx++] = ( inClose[i] >= inOpen[i] ? 1 : -1 ) * 100;
	         else
	            outInteger[outIdx++] = 0;
	         i++;
	      } while( i <= endIdx );
	      outNBElement.value = outIdx;
	      outBegIdx.value = startIdx;
	      return RetCode.Success ;
	}
	

	@Override
	public RetCode cdlHarami(int startIdx, int endIdx, double[] inOpen, double[] inHigh, double[] inLow,
			double[] inClose, MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		double BodyShortPeriodTotal, BodyLongPeriodTotal;
	      int i, outIdx, BodyShortTrailingIdx, BodyLongTrailingIdx, lookbackTotal;
	      if( startIdx < 0 )
	         return RetCode.OutOfRangeStartIndex ;
	      if( (endIdx < 0) || (endIdx < startIdx))
	         return RetCode.OutOfRangeEndIndex ;
	      lookbackTotal = cdlHaramiLookback ();
	      if( startIdx < lookbackTotal )
	         startIdx = lookbackTotal;
	      if( startIdx > endIdx )
	      {
	         outBegIdx.value = 0 ;
	         outNBElement.value = 0 ;
	         return RetCode.Success ;
	      }
	      BodyLongPeriodTotal = 0;
	      BodyShortPeriodTotal = 0;
	      BodyLongTrailingIdx = startIdx -1 - (this.candleSettings[CandleSettingType.BodyLong.ordinal()].avgPeriod) ;
	      BodyShortTrailingIdx = startIdx - (this.candleSettings[CandleSettingType.BodyShort.ordinal()].avgPeriod) ;
	      i = BodyLongTrailingIdx;
	      while( i < startIdx-1 ) {
	         BodyLongPeriodTotal += ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ;
	         i++;
	      }
	      i = BodyShortTrailingIdx;
	      while( i < startIdx ) {
	         BodyShortPeriodTotal += ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ;
	         i++;
	      }
	      i = startIdx;
	      outIdx = 0;
	      do
	      {
	         if( ( Math.abs ( inClose[i-1] - inOpen[i-1] ) ) > ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].avgPeriod) != 0.0? BodyLongPeriodTotal / (this.candleSettings[CandleSettingType.BodyLong.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-1] - inOpen[i-1] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-1] - inLow[i-1] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-1] - ( inClose[i-1] >= inOpen[i-1] ? inClose[i-1] : inOpen[i-1] ) ) + ( ( inClose[i-1] >= inOpen[i-1] ? inOpen[i-1] : inClose[i-1] ) - inLow[i-1] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) ) &&
	            ( Math.abs ( inClose[i] - inOpen[i] ) ) <= ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].avgPeriod) != 0.0? BodyShortPeriodTotal / (this.candleSettings[CandleSettingType.BodyShort.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) ) &&
	            (((inClose[i]) > (inOpen[i])) ? (inClose[i]) : (inOpen[i])) <= (((inClose[i-1]) > (inOpen[i-1])) ? (inClose[i-1]) : (inOpen[i-1])) &&
	            (((inClose[i]) < (inOpen[i])) ? (inClose[i]) : (inOpen[i])) >= (((inClose[i-1]) < (inOpen[i-1])) ? (inClose[i-1]) : (inOpen[i-1]))
	            )
	            outInteger[outIdx++] = - ( inClose[i-1] >= inOpen[i-1] ? 1 : -1 ) * 100;
	         else
	            outInteger[outIdx++] = 0;
	         BodyLongPeriodTotal += ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-1] - inOpen[i-1] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-1] - inLow[i-1] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-1] - ( inClose[i-1] >= inOpen[i-1] ? inClose[i-1] : inOpen[i-1] ) ) + ( ( inClose[i-1] >= inOpen[i-1] ? inOpen[i-1] : inClose[i-1] ) - inLow[i-1] ) : 0 ) ) ) - ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[BodyLongTrailingIdx] - ( inClose[BodyLongTrailingIdx] >= inOpen[BodyLongTrailingIdx] ? inClose[BodyLongTrailingIdx] : inOpen[BodyLongTrailingIdx] ) ) + ( ( inClose[BodyLongTrailingIdx] >= inOpen[BodyLongTrailingIdx] ? inOpen[BodyLongTrailingIdx] : inClose[BodyLongTrailingIdx] ) - inLow[BodyLongTrailingIdx] ) : 0 ) ) ) ;
	         BodyShortPeriodTotal += ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) - ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx] ) ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx] ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[BodyShortTrailingIdx] - ( inClose[BodyShortTrailingIdx] >= inOpen[BodyShortTrailingIdx] ? inClose[BodyShortTrailingIdx] : inOpen[BodyShortTrailingIdx] ) ) + ( ( inClose[BodyShortTrailingIdx] >= inOpen[BodyShortTrailingIdx] ? inOpen[BodyShortTrailingIdx] : inClose[BodyShortTrailingIdx] ) - inLow[BodyShortTrailingIdx] ) : 0 ) ) ) ;
	         i++;
	         BodyLongTrailingIdx++;
	         BodyShortTrailingIdx++;
	      } while( i <= endIdx );
	      outNBElement.value = outIdx;
	      outBegIdx.value = startIdx;
	      return RetCode.Success ;
	}
	

	@Override
	public RetCode cdlHaramiCross(int startIdx, int endIdx, double[] inOpen, double[] inHigh, double[] inLow,
			double[] inClose, MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		double BodyDojiPeriodTotal, BodyLongPeriodTotal;
	      int i, outIdx, BodyDojiTrailingIdx, BodyLongTrailingIdx, lookbackTotal;
	      if( startIdx < 0 )
	         return RetCode.OutOfRangeStartIndex ;
	      if( (endIdx < 0) || (endIdx < startIdx))
	         return RetCode.OutOfRangeEndIndex ;
	      lookbackTotal = cdlHaramiCrossLookback ();
	      if( startIdx < lookbackTotal )
	         startIdx = lookbackTotal;
	      if( startIdx > endIdx )
	      {
	         outBegIdx.value = 0 ;
	         outNBElement.value = 0 ;
	         return RetCode.Success ;
	      }
	      BodyLongPeriodTotal = 0;
	      BodyDojiPeriodTotal = 0;
	      BodyLongTrailingIdx = startIdx -1 - (this.candleSettings[CandleSettingType.BodyLong.ordinal()].avgPeriod) ;
	      BodyDojiTrailingIdx = startIdx - (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].avgPeriod) ;
	      i = BodyLongTrailingIdx;
	      while( i < startIdx-1 ) {
	         BodyLongPeriodTotal += ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ;
	         i++;
	      }
	      i = BodyDojiTrailingIdx;
	      while( i < startIdx ) {
	         BodyDojiPeriodTotal += ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ;
	         i++;
	      }
	      i = startIdx;
	      outIdx = 0;
	      do
	      {
	         if( ( Math.abs ( inClose[i-1] - inOpen[i-1] ) ) > ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].avgPeriod) != 0.0? BodyLongPeriodTotal / (this.candleSettings[CandleSettingType.BodyLong.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-1] - inOpen[i-1] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-1] - inLow[i-1] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-1] - ( inClose[i-1] >= inOpen[i-1] ? inClose[i-1] : inOpen[i-1] ) ) + ( ( inClose[i-1] >= inOpen[i-1] ? inOpen[i-1] : inClose[i-1] ) - inLow[i-1] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) ) &&
	            ( Math.abs ( inClose[i] - inOpen[i] ) ) <= ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].avgPeriod) != 0.0? BodyDojiPeriodTotal / (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) ) &&
	            (((inClose[i]) > (inOpen[i])) ? (inClose[i]) : (inOpen[i])) <= (((inClose[i-1]) > (inOpen[i-1])) ? (inClose[i-1]) : (inOpen[i-1])) &&
	            (((inClose[i]) < (inOpen[i])) ? (inClose[i]) : (inOpen[i])) >= (((inClose[i-1]) < (inOpen[i-1])) ? (inClose[i-1]) : (inOpen[i-1]))
	            )
	            outInteger[outIdx++] = - ( inClose[i-1] >= inOpen[i-1] ? 1 : -1 ) * 100;
	         else
	            outInteger[outIdx++] = 0;
	         BodyLongPeriodTotal += ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-1] - inOpen[i-1] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-1] - inLow[i-1] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-1] - ( inClose[i-1] >= inOpen[i-1] ? inClose[i-1] : inOpen[i-1] ) ) + ( ( inClose[i-1] >= inOpen[i-1] ? inOpen[i-1] : inClose[i-1] ) - inLow[i-1] ) : 0 ) ) ) - ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[BodyLongTrailingIdx] - inOpen[BodyLongTrailingIdx] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[BodyLongTrailingIdx] - inLow[BodyLongTrailingIdx] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[BodyLongTrailingIdx] - ( inClose[BodyLongTrailingIdx] >= inOpen[BodyLongTrailingIdx] ? inClose[BodyLongTrailingIdx] : inOpen[BodyLongTrailingIdx] ) ) + ( ( inClose[BodyLongTrailingIdx] >= inOpen[BodyLongTrailingIdx] ? inOpen[BodyLongTrailingIdx] : inClose[BodyLongTrailingIdx] ) - inLow[BodyLongTrailingIdx] ) : 0 ) ) ) ;
	         BodyDojiPeriodTotal += ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) - ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[BodyDojiTrailingIdx] - inOpen[BodyDojiTrailingIdx] ) ) : ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[BodyDojiTrailingIdx] - inLow[BodyDojiTrailingIdx] ) : ( (this.candleSettings[CandleSettingType.BodyDoji.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[BodyDojiTrailingIdx] - ( inClose[BodyDojiTrailingIdx] >= inOpen[BodyDojiTrailingIdx] ? inClose[BodyDojiTrailingIdx] : inOpen[BodyDojiTrailingIdx] ) ) + ( ( inClose[BodyDojiTrailingIdx] >= inOpen[BodyDojiTrailingIdx] ? inOpen[BodyDojiTrailingIdx] : inClose[BodyDojiTrailingIdx] ) - inLow[BodyDojiTrailingIdx] ) : 0 ) ) ) ;
	         i++;
	         BodyLongTrailingIdx++;
	         BodyDojiTrailingIdx++;
	      } while( i <= endIdx );
	      outNBElement.value = outIdx;
	      outBegIdx.value = startIdx;
	      return RetCode.Success ;
	}
	

	@Override
	public RetCode cdlHomingPigeon(int startIdx, int endIdx, double[] inOpen, double[] inHigh, double[] inLow,
			double[] inClose, MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		double BodyShortPeriodTotal, BodyLongPeriodTotal;
	      int i, outIdx, BodyShortTrailingIdx, BodyLongTrailingIdx, lookbackTotal;
	      if( startIdx < 0 )
	         return RetCode.OutOfRangeStartIndex ;
	      if( (endIdx < 0) || (endIdx < startIdx))
	         return RetCode.OutOfRangeEndIndex ;
	      lookbackTotal = cdlHomingPigeonLookback ();
	      if( startIdx < lookbackTotal )
	         startIdx = lookbackTotal;
	      if( startIdx > endIdx )
	      {
	         outBegIdx.value = 0 ;
	         outNBElement.value = 0 ;
	         return RetCode.Success ;
	      }
	      BodyLongPeriodTotal = 0;
	      BodyShortPeriodTotal = 0;
	      BodyLongTrailingIdx = startIdx - (this.candleSettings[CandleSettingType.BodyLong.ordinal()].avgPeriod) ;
	      BodyShortTrailingIdx = startIdx - (this.candleSettings[CandleSettingType.BodyShort.ordinal()].avgPeriod) ;
	      i = BodyLongTrailingIdx;
	      while( i < startIdx ) {
	         BodyLongPeriodTotal += ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-1] - inOpen[i-1] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-1] - inLow[i-1] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-1] - ( inClose[i-1] >= inOpen[i-1] ? inClose[i-1] : inOpen[i-1] ) ) + ( ( inClose[i-1] >= inOpen[i-1] ? inOpen[i-1] : inClose[i-1] ) - inLow[i-1] ) : 0 ) ) ) ;
	         i++;
	      }
	      i = BodyShortTrailingIdx;
	      while( i < startIdx ) {
	         BodyShortPeriodTotal += ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ;
	         i++;
	      }
	      i = startIdx;
	      outIdx = 0;
	      do
	      {
	         if( ( inClose[i-1] >= inOpen[i-1] ? 1 : -1 ) == -1 &&
	            ( inClose[i] >= inOpen[i] ? 1 : -1 ) == -1 &&
	            ( Math.abs ( inClose[i-1] - inOpen[i-1] ) ) > ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].avgPeriod) != 0.0? BodyLongPeriodTotal / (this.candleSettings[CandleSettingType.BodyLong.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-1] - inOpen[i-1] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-1] - inLow[i-1] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-1] - ( inClose[i-1] >= inOpen[i-1] ? inClose[i-1] : inOpen[i-1] ) ) + ( ( inClose[i-1] >= inOpen[i-1] ? inOpen[i-1] : inClose[i-1] ) - inLow[i-1] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) ) &&
	            ( Math.abs ( inClose[i] - inOpen[i] ) ) <= ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].avgPeriod) != 0.0? BodyShortPeriodTotal / (this.candleSettings[CandleSettingType.BodyShort.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) ) &&
	            inOpen[i] <= inOpen[i-1] &&
	            inClose[i] >= inClose[i-1]
	            )
	            outInteger[outIdx++] = 100;
	         else
	            outInteger[outIdx++] = 0;
	         BodyLongPeriodTotal += ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-1] - inOpen[i-1] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-1] - inLow[i-1] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-1] - ( inClose[i-1] >= inOpen[i-1] ? inClose[i-1] : inOpen[i-1] ) ) + ( ( inClose[i-1] >= inOpen[i-1] ? inOpen[i-1] : inClose[i-1] ) - inLow[i-1] ) : 0 ) ) ) - ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[BodyLongTrailingIdx-1] - inOpen[BodyLongTrailingIdx-1] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[BodyLongTrailingIdx-1] - inLow[BodyLongTrailingIdx-1] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[BodyLongTrailingIdx-1] - ( inClose[BodyLongTrailingIdx-1] >= inOpen[BodyLongTrailingIdx-1] ? inClose[BodyLongTrailingIdx-1] : inOpen[BodyLongTrailingIdx-1] ) ) + ( ( inClose[BodyLongTrailingIdx-1] >= inOpen[BodyLongTrailingIdx-1] ? inOpen[BodyLongTrailingIdx-1] : inClose[BodyLongTrailingIdx-1] ) - inLow[BodyLongTrailingIdx-1] ) : 0 ) ) ) ;
	         BodyShortPeriodTotal += ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) - ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[BodyShortTrailingIdx] - inOpen[BodyShortTrailingIdx] ) ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[BodyShortTrailingIdx] - inLow[BodyShortTrailingIdx] ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[BodyShortTrailingIdx] - ( inClose[BodyShortTrailingIdx] >= inOpen[BodyShortTrailingIdx] ? inClose[BodyShortTrailingIdx] : inOpen[BodyShortTrailingIdx] ) ) + ( ( inClose[BodyShortTrailingIdx] >= inOpen[BodyShortTrailingIdx] ? inOpen[BodyShortTrailingIdx] : inClose[BodyShortTrailingIdx] ) - inLow[BodyShortTrailingIdx] ) : 0 ) ) ) ;
	         i++;
	         BodyLongTrailingIdx++;
	         BodyShortTrailingIdx++;
	      } while( i <= endIdx );
	      outNBElement.value = outIdx;
	      outBegIdx.value = startIdx;
	      return RetCode.Success ;
	}

	@Override
	public RetCode cdlInvertedHammer(int startIdx, int endIdx, double[] inOpen, double[] inHigh, double[] inLow,
			double[] inClose, MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		double BodyPeriodTotal, ShadowLongPeriodTotal, ShadowVeryShortPeriodTotal;
	      int i, outIdx, BodyTrailingIdx, ShadowLongTrailingIdx, ShadowVeryShortTrailingIdx, lookbackTotal;
	      if( startIdx < 0 )
	         return RetCode.OutOfRangeStartIndex ;
	      if( (endIdx < 0) || (endIdx < startIdx))
	         return RetCode.OutOfRangeEndIndex ;
	      lookbackTotal = cdlInvertedHammerLookback ();
	      if( startIdx < lookbackTotal )
	         startIdx = lookbackTotal;
	      if( startIdx > endIdx )
	      {
	         outBegIdx.value = 0 ;
	         outNBElement.value = 0 ;
	         return RetCode.Success ;
	      }
	      BodyPeriodTotal = 0;
	      BodyTrailingIdx = startIdx - (this.candleSettings[CandleSettingType.BodyShort.ordinal()].avgPeriod) ;
	      ShadowLongPeriodTotal = 0;
	      ShadowLongTrailingIdx = startIdx - (this.candleSettings[CandleSettingType.ShadowLong.ordinal()].avgPeriod) ;
	      ShadowVeryShortPeriodTotal = 0;
	      ShadowVeryShortTrailingIdx = startIdx - (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].avgPeriod) ;
	      i = BodyTrailingIdx;
	      while( i < startIdx ) {
	         BodyPeriodTotal += ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ;
	         i++;
	      }
	      i = ShadowLongTrailingIdx;
	      while( i < startIdx ) {
	         ShadowLongPeriodTotal += ( (this.candleSettings[CandleSettingType.ShadowLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.ShadowLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.ShadowLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ;
	         i++;
	      }
	      i = ShadowVeryShortTrailingIdx;
	      while( i < startIdx ) {
	         ShadowVeryShortPeriodTotal += ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ;
	         i++;
	      }
	      outIdx = 0;
	      do
	      {
	         if( ( Math.abs ( inClose[i] - inOpen[i] ) ) < ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].avgPeriod) != 0.0? BodyPeriodTotal / (this.candleSettings[CandleSettingType.BodyShort.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) ) &&
	            ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) > ( (this.candleSettings[CandleSettingType.ShadowLong.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.ShadowLong.ordinal()].avgPeriod) != 0.0? ShadowLongPeriodTotal / (this.candleSettings[CandleSettingType.ShadowLong.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.ShadowLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.ShadowLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.ShadowLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.ShadowLong.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) ) &&
	            ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) < ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].avgPeriod) != 0.0? ShadowVeryShortPeriodTotal / (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) ) &&
	            ( (((inOpen[i]) > (inClose[i])) ? (inOpen[i]) : (inClose[i])) <= (((inOpen[i-1]) < (inClose[i-1])) ? (inOpen[i-1]) : (inClose[i-1])) ) )
	            outInteger[outIdx++] = 100;
	         else
	            outInteger[outIdx++] = 0;
	         BodyPeriodTotal += ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) )
	            - ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[BodyTrailingIdx] - inOpen[BodyTrailingIdx] ) ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[BodyTrailingIdx] - inLow[BodyTrailingIdx] ) : ( (this.candleSettings[CandleSettingType.BodyShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[BodyTrailingIdx] - ( inClose[BodyTrailingIdx] >= inOpen[BodyTrailingIdx] ? inClose[BodyTrailingIdx] : inOpen[BodyTrailingIdx] ) ) + ( ( inClose[BodyTrailingIdx] >= inOpen[BodyTrailingIdx] ? inOpen[BodyTrailingIdx] : inClose[BodyTrailingIdx] ) - inLow[BodyTrailingIdx] ) : 0 ) ) ) ;
	         ShadowLongPeriodTotal += ( (this.candleSettings[CandleSettingType.ShadowLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.ShadowLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.ShadowLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) )
	            - ( (this.candleSettings[CandleSettingType.ShadowLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[ShadowLongTrailingIdx] - inOpen[ShadowLongTrailingIdx] ) ) : ( (this.candleSettings[CandleSettingType.ShadowLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[ShadowLongTrailingIdx] - inLow[ShadowLongTrailingIdx] ) : ( (this.candleSettings[CandleSettingType.ShadowLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[ShadowLongTrailingIdx] - ( inClose[ShadowLongTrailingIdx] >= inOpen[ShadowLongTrailingIdx] ? inClose[ShadowLongTrailingIdx] : inOpen[ShadowLongTrailingIdx] ) ) + ( ( inClose[ShadowLongTrailingIdx] >= inOpen[ShadowLongTrailingIdx] ? inOpen[ShadowLongTrailingIdx] : inClose[ShadowLongTrailingIdx] ) - inLow[ShadowLongTrailingIdx] ) : 0 ) ) ) ;
	         ShadowVeryShortPeriodTotal += ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) )
	            - ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[ShadowVeryShortTrailingIdx] - inOpen[ShadowVeryShortTrailingIdx] ) ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[ShadowVeryShortTrailingIdx] - inLow[ShadowVeryShortTrailingIdx] ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[ShadowVeryShortTrailingIdx] - ( inClose[ShadowVeryShortTrailingIdx] >= inOpen[ShadowVeryShortTrailingIdx] ? inClose[ShadowVeryShortTrailingIdx] : inOpen[ShadowVeryShortTrailingIdx] ) ) + ( ( inClose[ShadowVeryShortTrailingIdx] >= inOpen[ShadowVeryShortTrailingIdx] ? inOpen[ShadowVeryShortTrailingIdx] : inClose[ShadowVeryShortTrailingIdx] ) - inLow[ShadowVeryShortTrailingIdx] ) : 0 ) ) ) ;
	         i++;
	         BodyTrailingIdx++;
	         ShadowLongTrailingIdx++;
	         ShadowVeryShortTrailingIdx++;
	      } while( i <= endIdx );
	      outNBElement.value = outIdx;
	      outBegIdx.value = startIdx;
	      return RetCode.Success ;
	}

	@Override
	public RetCode cdlKicking(int startIdx, int endIdx, double[] inOpen, double[] inHigh, double[] inLow,
			double[] inClose, MInteger outBegIdx, MInteger outNBElement, int[] outInteger) {
		double []ShadowVeryShortPeriodTotal = new double[2] ;
	      double []BodyLongPeriodTotal = new double[2] ;
	      int i, outIdx, totIdx, ShadowVeryShortTrailingIdx, BodyLongTrailingIdx, lookbackTotal;
	      if( startIdx < 0 )
	         return RetCode.OutOfRangeStartIndex ;
	      if( (endIdx < 0) || (endIdx < startIdx))
	         return RetCode.OutOfRangeEndIndex ;
	      lookbackTotal = cdlKickingLookback ();
	      if( startIdx < lookbackTotal )
	         startIdx = lookbackTotal;
	      if( startIdx > endIdx )
	      {
	         outBegIdx.value = 0 ;
	         outNBElement.value = 0 ;
	         return RetCode.Success ;
	      }
	      ShadowVeryShortPeriodTotal[1] = 0;
	      ShadowVeryShortPeriodTotal[0] = 0;
	      ShadowVeryShortTrailingIdx = startIdx - (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].avgPeriod) ;
	      BodyLongPeriodTotal[1] = 0;
	      BodyLongPeriodTotal[0] = 0;
	      BodyLongTrailingIdx = startIdx - (this.candleSettings[CandleSettingType.BodyLong.ordinal()].avgPeriod) ;
	      i = ShadowVeryShortTrailingIdx;
	      while( i < startIdx ) {
	         ShadowVeryShortPeriodTotal[1] += ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-1] - inOpen[i-1] ) ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-1] - inLow[i-1] ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-1] - ( inClose[i-1] >= inOpen[i-1] ? inClose[i-1] : inOpen[i-1] ) ) + ( ( inClose[i-1] >= inOpen[i-1] ? inOpen[i-1] : inClose[i-1] ) - inLow[i-1] ) : 0 ) ) ) ;
	         ShadowVeryShortPeriodTotal[0] += ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ;
	         i++;
	      }
	      i = BodyLongTrailingIdx;
	      while( i < startIdx ) {
	         BodyLongPeriodTotal[1] += ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-1] - inOpen[i-1] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-1] - inLow[i-1] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-1] - ( inClose[i-1] >= inOpen[i-1] ? inClose[i-1] : inOpen[i-1] ) ) + ( ( inClose[i-1] >= inOpen[i-1] ? inOpen[i-1] : inClose[i-1] ) - inLow[i-1] ) : 0 ) ) ) ;
	         BodyLongPeriodTotal[0] += ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ;
	         i++;
	      }
	      i = startIdx;
	      outIdx = 0;
	      do
	      {
	         if( ( inClose[i-1] >= inOpen[i-1] ? 1 : -1 ) == - ( inClose[i] >= inOpen[i] ? 1 : -1 ) &&
	            ( Math.abs ( inClose[i-1] - inOpen[i-1] ) ) > ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].avgPeriod) != 0.0? BodyLongPeriodTotal[1] / (this.candleSettings[CandleSettingType.BodyLong.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-1] - inOpen[i-1] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-1] - inLow[i-1] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-1] - ( inClose[i-1] >= inOpen[i-1] ? inClose[i-1] : inOpen[i-1] ) ) + ( ( inClose[i-1] >= inOpen[i-1] ? inOpen[i-1] : inClose[i-1] ) - inLow[i-1] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) ) &&
	            ( inHigh[i-1] - ( inClose[i-1] >= inOpen[i-1] ? inClose[i-1] : inOpen[i-1] ) ) < ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].avgPeriod) != 0.0? ShadowVeryShortPeriodTotal[1] / (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-1] - inOpen[i-1] ) ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-1] - inLow[i-1] ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-1] - ( inClose[i-1] >= inOpen[i-1] ? inClose[i-1] : inOpen[i-1] ) ) + ( ( inClose[i-1] >= inOpen[i-1] ? inOpen[i-1] : inClose[i-1] ) - inLow[i-1] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) ) &&
	            ( ( inClose[i-1] >= inOpen[i-1] ? inOpen[i-1] : inClose[i-1] ) - inLow[i-1] ) < ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].avgPeriod) != 0.0? ShadowVeryShortPeriodTotal[1] / (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-1] - inOpen[i-1] ) ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-1] - inLow[i-1] ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-1] - ( inClose[i-1] >= inOpen[i-1] ? inClose[i-1] : inOpen[i-1] ) ) + ( ( inClose[i-1] >= inOpen[i-1] ? inOpen[i-1] : inClose[i-1] ) - inLow[i-1] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) ) &&
	            ( Math.abs ( inClose[i] - inOpen[i] ) ) > ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].avgPeriod) != 0.0? BodyLongPeriodTotal[0] / (this.candleSettings[CandleSettingType.BodyLong.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) ) &&
	            ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) < ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].avgPeriod) != 0.0? ShadowVeryShortPeriodTotal[0] / (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) ) &&
	            ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) < ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].factor) * ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].avgPeriod) != 0.0? ShadowVeryShortPeriodTotal[0] / (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].avgPeriod) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i] - inOpen[i] ) ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i] - inLow[i] ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i] - ( inClose[i] >= inOpen[i] ? inClose[i] : inOpen[i] ) ) + ( ( inClose[i] >= inOpen[i] ? inOpen[i] : inClose[i] ) - inLow[i] ) : 0 ) ) ) ) / ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? 2.0 : 1.0 ) ) &&
	            (
	            ( ( inClose[i-1] >= inOpen[i-1] ? 1 : -1 ) == -1 && ( inLow[i] >= inHigh[i-1] ) )
	            ||
	            ( ( inClose[i-1] >= inOpen[i-1] ? 1 : -1 ) == 1 && ( inHigh[i] <= inLow[i-1] ) )
	            )
	            )
	            outInteger[outIdx++] = ( inClose[i] >= inOpen[i] ? 1 : -1 ) * 100;
	         else
	            outInteger[outIdx++] = 0;
	         for (totIdx = 1; totIdx >= 0; --totIdx) {
	            BodyLongPeriodTotal[totIdx] += ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-totIdx] - inOpen[i-totIdx] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-totIdx] - inLow[i-totIdx] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-totIdx] - ( inClose[i-totIdx] >= inOpen[i-totIdx] ? inClose[i-totIdx] : inOpen[i-totIdx] ) ) + ( ( inClose[i-totIdx] >= inOpen[i-totIdx] ? inOpen[i-totIdx] : inClose[i-totIdx] ) - inLow[i-totIdx] ) : 0 ) ) )
	               - ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[BodyLongTrailingIdx-totIdx] - inOpen[BodyLongTrailingIdx-totIdx] ) ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[BodyLongTrailingIdx-totIdx] - inLow[BodyLongTrailingIdx-totIdx] ) : ( (this.candleSettings[CandleSettingType.BodyLong.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[BodyLongTrailingIdx-totIdx] - ( inClose[BodyLongTrailingIdx-totIdx] >= inOpen[BodyLongTrailingIdx-totIdx] ? inClose[BodyLongTrailingIdx-totIdx] : inOpen[BodyLongTrailingIdx-totIdx] ) ) + ( ( inClose[BodyLongTrailingIdx-totIdx] >= inOpen[BodyLongTrailingIdx-totIdx] ? inOpen[BodyLongTrailingIdx-totIdx] : inClose[BodyLongTrailingIdx-totIdx] ) - inLow[BodyLongTrailingIdx-totIdx] ) : 0 ) ) ) ;
	            ShadowVeryShortPeriodTotal[totIdx] += ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[i-totIdx] - inOpen[i-totIdx] ) ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[i-totIdx] - inLow[i-totIdx] ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[i-totIdx] - ( inClose[i-totIdx] >= inOpen[i-totIdx] ? inClose[i-totIdx] : inOpen[i-totIdx] ) ) + ( ( inClose[i-totIdx] >= inOpen[i-totIdx] ? inOpen[i-totIdx] : inClose[i-totIdx] ) - inLow[i-totIdx] ) : 0 ) ) )
	               - ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.RealBody ? ( Math.abs ( inClose[ShadowVeryShortTrailingIdx-totIdx] - inOpen[ShadowVeryShortTrailingIdx-totIdx] ) ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.HighLow ? ( inHigh[ShadowVeryShortTrailingIdx-totIdx] - inLow[ShadowVeryShortTrailingIdx-totIdx] ) : ( (this.candleSettings[CandleSettingType.ShadowVeryShort.ordinal()].rangeType) == RangeType.Shadows ? ( inHigh[ShadowVeryShortTrailingIdx-totIdx] - ( inClose[ShadowVeryShortTrailingIdx-totIdx] >= inOpen[ShadowVeryShortTrailingIdx-totIdx] ? inClose[ShadowVeryShortTrailingIdx-totIdx] : inOpen[ShadowVeryShortTrailingIdx-totIdx] ) ) + ( ( inClose[ShadowVeryShortTrailingIdx-totIdx] >= inOpen[ShadowVeryShortTrailingIdx-totIdx] ? inOpen[ShadowVeryShortTrailingIdx-totIdx] : inClose[ShadowVeryShortTrailingIdx-totIdx] ) - inLow[ShadowVeryShortTrailingIdx-totIdx] ) : 0 ) ) ) ;
	         }
	         i++;
	         ShadowVeryShortTrailingIdx++;
	         BodyLongTrailingIdx++;
	      } while( i <= endIdx );
	      outNBElement.value = outIdx;
	      outBegIdx.value = startIdx;
	      return RetCode.Success ;
	}
	
	
	
	
	

}
