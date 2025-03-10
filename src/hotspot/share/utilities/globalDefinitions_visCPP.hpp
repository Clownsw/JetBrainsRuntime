/*
 * Copyright (c) 1997, 2024, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 *
 */

#ifndef SHARE_UTILITIES_GLOBALDEFINITIONS_VISCPP_HPP
#define SHARE_UTILITIES_GLOBALDEFINITIONS_VISCPP_HPP

#include "jni.h"

// This file holds compiler-dependent includes,
// globally used constants & types, class (forward)
// declarations and a few frequently used utility functions.

# include <ctype.h>
# include <string.h>
# include <stdarg.h>
# include <stdlib.h>
# include <stdint.h>
# include <stddef.h>// for offsetof
# include <sys/stat.h>
# include <io.h>    // for stream.cpp
# include <float.h> // for _isnan
# include <stdio.h> // for va_list
# include <time.h>
# include <fcntl.h>
# include <limits.h>
# include <inttypes.h>
// Need this on windows to get the math constants (e.g., M_PI).
#define _USE_MATH_DEFINES
# include <math.h>

// Only 64-bit Windows is supported
#ifndef _LP64
#error unsupported platform
#endif

// 4810578: varargs unsafe on 32-bit integer/64-bit pointer architectures
// When __cplusplus is defined, NULL is defined as 0 (32-bit constant) in
// system header files.  On 32-bit architectures, there is no problem.
// On 64-bit architectures, defining NULL as a 32-bit constant can cause
// problems with varargs functions: C++ integral promotion rules say for
// varargs, we pass the argument 0 as an int.  So, if NULL was passed to a
// varargs function it will remain 32-bits.  Depending on the calling
// convention of the machine, if the argument is passed on the stack then
// only 32-bits of the "NULL" pointer may be initialized to zero.  The
// other 32-bits will be garbage.  If the varargs function is expecting a
// pointer when it extracts the argument, then we may have a problem.
//
// Solution: For 64-bit architectures, redefine NULL as 64-bit constant 0.
#undef NULL
// 64-bit Windows uses a P64 data model (not LP64, although we define _LP64)
// Since longs are 32-bit we cannot use 0L here.  Use the Visual C++ specific
// 64-bit integer-suffix (LL) instead.
#define NULL 0LL

typedef int64_t ssize_t;

// Non-standard stdlib-like stuff:
inline int strcasecmp(const char *s1, const char *s2) { return _stricmp(s1,s2); }
inline int strncasecmp(const char *s1, const char *s2, size_t n) {
  return _strnicmp(s1,s2,n);
}

// VS doesn't provide strtok_r, which is a POSIX function.  Instead, it
// provides the same function under the name strtok_s.  Note that this is
// *not* the same as the C99 Annex K strtok_s.  VS provides that function
// under the name strtok_s_l.  Make strtok_r a synonym so we can use that name
// in shared code.
const auto strtok_r = strtok_s;

// VS doesn't provide POSIX macros S_ISFIFO or S_IFIFO.  It doesn't even
// provide _S_ISFIFO, per its usual naming convention for POSIX stuff.  But it
// does provide _S_IFIFO, so we can roll our own S_ISFIFO.
#define S_ISFIFO(mode) (((mode) & _S_IFIFO) == _S_IFIFO)

// Checking for nanness

inline int g_isnan(jfloat  f)                    { return _isnan(f); }
inline int g_isnan(jdouble f)                    { return _isnan(f); }

// Checking for finiteness

inline int g_isfinite(jfloat  f)                 { return _finite(f); }
inline int g_isfinite(jdouble f)                 { return _finite(f); }

// Miscellaneous

// Visual Studio 2005 deprecates POSIX names - use ISO C++ names instead
#define wopen _wopen

// Formatting.
#define FORMAT64_MODIFIER "ll"

#define offset_of(klass,field) offsetof(klass,field)

#define THREAD_LOCAL __declspec(thread)

// Inlining support
// MSVC has '__declspec(noinline)' but according to the official documentation
// it only applies to member functions. There are reports though which pretend
// that it also works for freestanding functions.
#define NOINLINE     __declspec(noinline)
#define ALWAYSINLINE __forceinline

#ifdef _M_ARM64
#define USE_VECTORED_EXCEPTION_HANDLING
#endif

#ifndef SSIZE_MAX
#define SSIZE_MIN LLONG_MIN
#define SSIZE_MAX LLONG_MAX
#endif // SSIZE_MAX missing

#endif // SHARE_UTILITIES_GLOBALDEFINITIONS_VISCPP_HPP
