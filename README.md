# cfr
Identify molecules with similar biological activity by analysing electrostatic potential maps

A long time ago, in a galaxy far far away... I did this program as part of my thesis of the course "Rational drug design" during my master in Bioinformatics. It is an experimental algorithm that I have studied in collaboration with my tutor Roberto Forlani at Nikem Reasearch during a stage period. It is a virtual screening program that uses electrostatic potential as molecular descriptor. The particularity is that descriptors are dynamically computated by clustering homogeneous electron negative regions in the ESP map, then a vector backbone describing the molecule is applied to compared molecule, oriented by computing a translation dihedral angle and than a similarity score % is given in output. You can find the details in my thesis and it's presentation.
The ESP maps are externally computed by Arguslab, a free available molecular modeling program, then parsed by the readGrid program by my course mate Damiano Somenzi and then given in input to cfr. 

It was my very first program in java, now I realize that has many stylistic issues, sorry about that. It just represent my first experience in this world, done more than ten years ago and has some romantic flavour for me. I don't exclude I will do some refactoring in the future but I cannot assure it.
I have put it here because I realized that it has been closed in a drawer for many years, so I thought: why don't put it on internet? Someone may be interested in it and can bring it to new life. If you are interested in extend it or use it, I only require you to drop me an email (find it in the colophon in my home page) and cite my name. That's all. I can also give you some hints on how to run whole pipeline.

It the source is included an old version of the Weka library of University of Waikato. I have no right on that I have just included it because it has an obsolete api.
