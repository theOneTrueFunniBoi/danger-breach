Type Difficulty
	Field name$
	Field description$
	Field permaDeath%
	Field aggressiveNPCs
	Field saveType%
	Field otherFactors%
	
	Field r%
	Field g%
	Field b%
	
	Field customizable%
End Type

Dim difficulties.Difficulty(4)

Global SelectedDifficulty.Difficulty;Global str_diff_easy$,str_diff_med$,str_diff_hard$,str_diff_custom$
;Global str_diff_easy_desc$,str_diff_med_desc$,str_diff_med_desc2$
;Global str_diff_hard_desc$,str_diff_hard_desc2$

Const SAFE=0, EUCLID=1, KETER=2, CUSTOM=3

Const SAVEANYWHERE = 0, SAVEONQUIT=1, SAVEONSCREENS=2

Const EASY = 0, NORMAL = 1, HARD = 2

;Const TotalDifficulties = 4

difficulties(SAFE) = New Difficulty
;difficulties(SAFE)\name = str_diff_easy
;difficulties(SAFE)\description = str_diff_easy_desc
difficulties(SAFE)\permaDeath = False
difficulties(SAFE)\aggressiveNPCs = False
difficulties(SAFE)\saveType = SAVEANYWHERE
difficulties(SAFE)\otherFactors = EASY
difficulties(SAFE)\r = 120
difficulties(SAFE)\g = 150
difficulties(SAFE)\b = 50

difficulties(EUCLID) = New Difficulty
;difficulties(EUCLID)\name = str_diff_med
;difficulties(EUCLID)\description = str_diff_med_desc
;difficulties(EUCLID)\description = difficulties(EUCLID)\description +" "+str_diff_med_desc2
difficulties(EUCLID)\permaDeath = False
difficulties(EUCLID)\aggressiveNPCs = False
difficulties(EUCLID)\saveType = SAVEONSCREENS
difficulties(EUCLID)\otherFactors = NORMAL
difficulties(EUCLID)\r = 200
difficulties(EUCLID)\g = 200
difficulties(EUCLID)\b = 0

difficulties(KETER) = New Difficulty
;difficulties(KETER)\name = str_diff_hard
;difficulties(KETER)\description = str_diff_hard_desc
;difficulties(KETER)\description = difficulties(KETER)\description +" "+str_diff_hard_desc2
difficulties(KETER)\permaDeath = True
difficulties(KETER)\aggressiveNPCs = True
difficulties(KETER)\saveType = SAVEONQUIT
difficulties(KETER)\otherFactors = HARD
difficulties(KETER)\r = 200
difficulties(KETER)\g = 0
difficulties(KETER)\b = 0

difficulties(CUSTOM) = New Difficulty
;difficulties(CUSTOM)\name = str_diff_custom
difficulties(CUSTOM)\permaDeath = False
difficulties(CUSTOM)\aggressiveNPCs = True
difficulties(CUSTOM)\saveType = SAVEANYWHERE
difficulties(CUSTOM)\customizable = True
difficulties(CUSTOM)\otherFactors = EASY
difficulties(CUSTOM)\r = 255
difficulties(CUSTOM)\g = 255
difficulties(CUSTOM)\b = 255

SelectedDifficulty = difficulties(EUCLID)
;~IDEal Editor Parameters:
;~F#0
;~C#Blitz3D