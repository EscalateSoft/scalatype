val s1 = Set(1,2,3)
val s2 = Set("Hello", "CRS", "Types")

def stringInSet(s: String, set: Set[String]): Boolean =
  set.contains(s)

stringInSet("CRS", s2) // true

stringInSet("CRS", s1)





def lengthOfSet(set: Set[Any]): Int = set.size

lengthOfSet(s1)



lengthOfSet(s2)

