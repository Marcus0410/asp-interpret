# Finn 1. påskedag for årene 2021-2025.

y = 2021
while y <= 2025:
   a = y % 19
   b = y // 100
   c = y % 100
   d = b // 4
   e = b % 4
   f = (b+8) // 25
   g = (b-f+1) // 3
   h = (19*a+b-d-g+15) % 30
   i = c // 4
   k = c % 4
   l = (32+2*e+2*i-h-k) % 7
   m = (a+11*h+22*l) // 451
   
   month = (h+l-7*m+114) // 31
   day = (h+l-7*m+114) % 31 + 1
   if month == 3:
      print(str(day) + ". mars", y)
   else:
      print(str(day) + ". april", y)
   y = y+1
