def title_case():
    sentence = input('String?: ')
    sentence = sentence.split()
    ignore = ['and', 'the', 'for']
    sentence = [x[0].upper() + x[1:] if x not in ignore or i == 0 else x 
                  for i,x in enumerate(sentence)]
    print(' '.join(sentence))
    
title_case()