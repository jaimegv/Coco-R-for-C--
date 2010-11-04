rem prueba4.bas (incorrecta)

sub multiplicar(a, b)
	let resultado = a * b
end sub

print "Ingrese un numero:"
input a

print "Ingrese un numero:"
rem error al escribir input
inpu b

call multiplicar(a, b)
print "La multiplicacion es: " ; resultado

end
