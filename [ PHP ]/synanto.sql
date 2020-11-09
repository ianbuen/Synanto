-- phpMyAdmin SQL Dump
-- version 4.4.14
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Jan 15, 2017 at 09:12 PM
-- Server version: 5.6.26
-- PHP Version: 5.6.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `synanto`
--

-- --------------------------------------------------------

--
-- Table structure for table `tbl_accounts`
--

CREATE TABLE IF NOT EXISTS `tbl_accounts` (
  `userID` int(11) NOT NULL,
  `username` varchar(20) NOT NULL,
  `password` varchar(20) NOT NULL,
  `firstname` varchar(30) NOT NULL,
  `lastname` varchar(30) NOT NULL,
  `email` varchar(30) NOT NULL,
  `fcmToken` varchar(255) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `tbl_accounts`
--

INSERT INTO `tbl_accounts` (`userID`, `username`, `password`, `firstname`, `lastname`, `email`, `fcmToken`) VALUES
(1, 'ianbuen', 'aira143', 'Ian', 'Buenconsejo', 'ian1337@leet.com', NULL),
(2, 'johncena', 'cenation', 'John', 'Cena', 'thuganomics101@wwe.com', NULL),
(3, 'mustardbeats', '123456', 'Mustard Beats', 'Miller', 'mbeats@mail.com', NULL),
(4, 'foobar', '123qwe', 'foo', 'bar', 'foo@bar.com', NULL),
(5, 'bernard', 'bernard', 'bernard', 'pelobello', 'lloyd@yahoo.com', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `tbl_events`
--

CREATE TABLE IF NOT EXISTS `tbl_events` (
  `eventID` int(11) NOT NULL,
  `eventName` varchar(50) NOT NULL,
  `eventDescription` varchar(255) NOT NULL,
  `eventDate` varchar(30) NOT NULL,
  `eventTime` varchar(30) NOT NULL,
  `eventVenue` varchar(30) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `tbl_events`
--

INSERT INTO `tbl_events` (`eventID`, `eventName`, `eventDescription`, `eventDate`, `eventTime`, `eventVenue`) VALUES
(17, 'haha', 'hehe', '10/23/2016 (Sunday)', '3:12 PM', 'S201'),
(18, 'a', 'ee', '10/23/2016 (Sunday)', '3:15 PM', 'S201'),
(19, 'd', 'dd', '10/12/2016 (Wednesday)', '3:22 PM', 'S201'),
(20, 'r', 'r', '10/25/2016 (Tuesday)', '3:37 PM', 'S201'),
(21, 'j', 'h', '10/17/2016 (Monday)', '3:49 PM', 'S201'),
(22, 'test', 'test', '10/23/2016 (Sunday)', '7:20 PM', 'S203'),
(23, 'hu', 'shdc', '10/20/2016 (Thursday)', '7:23 PM', 'S205'),
(24, 'Band Practice', 'Jamming lang', '10/23/2016 (Sunday)', '8:31 PM', 'S205'),
(25, 'Live Performance', 'Trapeze, Circus, Tightrope', '10/24/2016 (Monday)', '1:48 AM', 'S203'),
(26, 'Pizza Party', 'Yellow Cab', '10/31/2016 (Monday)', '5:30 PM', 'G/F Canteen'),
(27, 'redefence', 'thesis', '10/24/2016 (Monday)', '1:11 PM', 'Multi Purpose Hall'),
(28, 'sample', 'sample', '10/24/2016 (Monday)', '1:12 PM', 'Assembly Area'),
(29, 'test', 'test', '10/26/2016 (Wednesday)', '3:15 PM', 'Auditorium'),
(30, 'Practice', 'Java', '10/24/2016 (Monday)', '6:19 PM', 'Sotero Main Lobby');

-- --------------------------------------------------------

--
-- Table structure for table `tbl_participants`
--

CREATE TABLE IF NOT EXISTS `tbl_participants` (
  `eventID` int(11) NOT NULL,
  `userID` int(11) NOT NULL,
  `isHost` tinyint(1) NOT NULL,
  `userLocation` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `tbl_participants`
--

INSERT INTO `tbl_participants` (`eventID`, `userID`, `isHost`, `userLocation`) VALUES
(17, 1, 1, '14.2901407,120.9152026'),
(17, 2, 0, '14.2906659,120.915106'),
(18, 1, 1, '14.2901407,120.9152026'),
(18, 2, 0, '14.2906659,120.915106'),
(19, 1, 1, '14.2901407,120.9152026'),
(19, 3, 0, NULL),
(19, 2, 0, '14.2906659,120.915106'),
(20, 1, 1, '14.2901407,120.9152026'),
(20, 2, 0, '14.2906659,120.915106'),
(20, 3, 0, NULL),
(21, 1, 1, '14.2901407,120.9152026'),
(21, 2, 0, '14.2906659,120.915106'),
(21, 3, 0, NULL),
(22, 1, 1, '14.2901407,120.9152026'),
(22, 2, 0, '14.2906659,120.915106'),
(22, 3, 0, NULL),
(23, 1, 1, '14.2901407,120.9152026'),
(17, 3, 0, NULL),
(23, 3, 0, NULL),
(23, 2, 0, '14.2906659,120.915106'),
(18, 3, 0, NULL),
(24, 2, 1, '14.2906659,120.915106'),
(24, 1, 0, '14.2901407,120.9152026'),
(24, 3, 0, NULL),
(24, 4, 0, NULL),
(0, 0, 0, NULL),
(25, 2, 1, '14.2906659,120.915106'),
(25, 1, 0, '14.2901407,120.9152026'),
(25, 3, 0, NULL),
(25, 4, 0, NULL),
(26, 2, 1, NULL),
(26, 3, 0, NULL),
(26, 4, 0, NULL),
(26, 1, 0, '14.2901407,120.9152026'),
(27, 1, 1, '14.2901407,120.9152026'),
(27, 2, 0, NULL),
(27, 3, 0, NULL),
(27, 4, 0, NULL),
(28, 1, 1, '14.2901407,120.9152026'),
(28, 2, 0, NULL),
(28, 3, 0, NULL),
(28, 4, 0, NULL),
(29, 1, 1, '14.2901407,120.9152026'),
(29, 2, 0, NULL),
(29, 3, 0, NULL),
(29, 4, 0, NULL),
(30, 1, 1, '14.2901407,120.9152026'),
(30, 5, 0, '14.2905882,120.9152486'),
(0, 0, 0, NULL),
(0, 0, 0, NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tbl_accounts`
--
ALTER TABLE `tbl_accounts`
  ADD PRIMARY KEY (`userID`);

--
-- Indexes for table `tbl_events`
--
ALTER TABLE `tbl_events`
  ADD PRIMARY KEY (`eventID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tbl_accounts`
--
ALTER TABLE `tbl_accounts`
  MODIFY `userID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=6;
--
-- AUTO_INCREMENT for table `tbl_events`
--
ALTER TABLE `tbl_events`
  MODIFY `eventID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=31;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
