<?php

    // A-STAR Algorithm
    // Implemented in PHP by Ian Paul T. Buenconsejo.
    // CS 401 - LPU Cavite

    header('Content-Type: text/javascript');

   // FOR TESTING ONLY, Node 73
   // $_POST['latA'] = '14.291682740552318';
   // $_POST['lonA'] = '120.91557404592572';

  // $_POST['latA'] = '14.290541155165812';
  // $_POST['lonA'] = '120.91562191152752';
   
  // $_POST['latB'] = '14.291217149848364';
  // $_POST['lonB'] = '120.91578874632683';
  // $_POST['floor'] = '2';
   
   // Node 36
   // $_POST['latB'] = '14.290317942707432';
   // $_POST['lonB'] = '120.9152518594642';

  // $_SERVER['REQUEST_METHOD'] = 'POST';

    if ($_SERVER['REQUEST_METHOD']=='POST') {
        $aStar = new AStar();
    }

    class AStar
    {
        public $nodes;
        public $path;

        public $src;
        public $dst;

        function __construct()
        {
            $this->setStartAndGoal();

            $this->getFromNodeList();

            $this->path = $this->findPath();

            if ($this->path != null) {
                echo json_encode(array("success"=>1, "path"=>$this->path));
            } else {
                echo json_encode(array("success"=>0));
            }
        }

        function setStartAndGoal() {
            $this->src = array();
            $this->src['lat'] = $_POST['latA'];
            $this->src['lon'] = $_POST['lonA'];

            $this->dst = array();
            $this->dst['lat'] = $_POST['latB'];
            $this->dst['lon'] = $_POST['lonB'];
        }

        function findPath() {
            $start =& $this->findNodeByLocation($this->src);
            $goal =& $this->findNodeByLocation($this->dst);

            $open = array();
            $closed = array();

            $checked = array();

            $start['h'] = $this->getDistance($start, $goal);
            $start['f'] = $start['h'];

            $open =& addToList($start, $open);

            while (sizeof($open) > 0) {
                $curr = $this->findLeastNode($open);

                if (array_search($curr['id'], $checked) === false)
                    $checked = addToList($curr['id'], $checked);

                $open =& removeFromList($curr, $open);
                $closed =& addToList($curr, $closed);

                if ($curr['id'] == $goal['id']) {
                    $path = array();

                    $temp =& $curr;
                    $length = 1;

                    while ($temp['id'] != $start['id']) {
                        $path[] = array("id"=>$temp['id'],
                                        "latitude"=>$temp['loc']['lat'],
                                        "longitude"=>$temp['loc']['lon']);
                        $temp =& $temp['parent'];
                        $length++;
                    }

                    $path[] = array("id"=>$start['id'],
                                    "latitude"=>$start['loc']['lat'],
                                    "longitude"=>$start['loc']['lon']);

                    return $path;
                }


                $curr =& $this->getSuccessors($curr);

                for ($i = 0; $i < sizeof($curr['successors']); $i++) {
                    $successor = $curr['successors'][$i];

                    $successor['g'] = $curr['g'] + $this->getDistance($curr, $successor);
                    $successor['h'] = $this->getDistance($successor, $goal);
                    $successor['f'] = $successor['g'] + $successor['h'];
                    $successor['parent'] = $curr;

                    if (isInList($successor, $open)) {
                        if ($successor['g'] <= $successor['f']) {
                            continue;
                        }

                    } else if (isInList($successor, $closed)) {
                        if ($successor['g'] <= $successor['f']) {
                            continue;
                        }

                        $open =& addToList($successor, $open);
                        $closed =& removeFromList($successor, $closed);
                    } else {
                        $open =& addToList($successor, $open);
                    }
                }
            }

            return null;
        }

        function findLeastNode(array $open) {
//            echo "\nOpen List: ";
//            foreach ($open as $node) {
//                echo $node['id'] . " (F: ".$node['f'].") ";
//            } echo "\n";

            $curr = $open[0];

            $count = 0;
            foreach ($open as $node) {
//                echo "count: " . $count . " | ";
                if ($node['id'] == $curr['id']) {
//                    echo "curr: " . $curr['id'];
//                    echo " | node: " . $node['id'] . "\n";
                    $count++;
                    continue;
                } else if ($node['f'] < $curr['f']) {
                    $curr = $node;
                }

//                echo "curr: " . $curr['id'];
//                echo " | node: " . $node['id'] . "\n";

                $count++;
            }

//            echo "\nNode ".$curr['id']." is chosen.\n";

            return $curr;
        }

        function findNodeByLocation(array $loc) {
            for ($i = 0; $i < sizeof($this->nodes); $i++) {
                if ($this->nodes[$i]['loc']['lat'] == $loc['lat'] &&
                    $this->nodes[$i]['loc']['lon'] == $loc['lon']) {
                    return $this->nodes[$i];
                }
            }

            return null;
        }

        function getDistance(array $a, array $b) {
            /* Euclidean Distance */
            $sum = 0;

            $sum = $sum + pow(floatval($a['loc']['lon'])
                        - floatval($b['loc']['lon']), 2);
            $sum = $sum + pow(floatval($a['loc']['lat'])
                        - floatval($b['loc']['lat']), 2);

            return sqrt($sum);
        }

	// Alternative Distance Formula
        // function getDistance(array $a, array $b) {
            // /* Haversine Formula */
            // $earthRad = 6371000;
            // $diffLat = deg2rad($b['loc']['lat']) - deg2rad($a['loc']['lat']);
            // $diffLon = deg2rad($b['loc']['lon']) - deg2rad($a['loc']['lon']);
            // $d = sin($diffLat / 2) * sin($diffLat / 2) +
                 // cos(deg2rad($a['loc']['lat'])) * cos(deg2rad($b['loc']['lat']))  *
                 // sin($diffLon / 2) * sin($diffLon / 2);
            // $c = 2 * asin(sqrt($d));
            // return $earthRad * $c;
        // }

        function getFromNodeList() {
            $this->nodes = array();

			switch(intval($_POST['floor'])) {
				case 1: $filename = "nodes/nodes-1f.txt"; break;
				case 2: $filename = "nodes/nodes-2f.txt"; break;
				case 3: $filename = "nodes/nodes-3f.txt"; break;
				case 4: $filename = "nodes/nodes-4f.txt";
			}
			
            $handle = fopen($filename, "r");
            if ($handle) {
                    while (($line = fgets($handle)) !== false) {
                        $nodeID = intval(substr($line, 1, strpos($line, ':')));

                        $line = trim(substr($line, strpos($line, ':')+1, strlen($line)));

                    $location = array();
                    $location['lat'] = substr($line, 0, strpos($line, ','));
                    $location['lon'] = substr($line, strpos($line, ',')+1, strlen($line));

                    $this->nodes[] = array('id'=>$nodeID,
                                           'loc'=>$location,
                                           'g'=>0, 'h'=>0, 'f'=>0,
                                           'parent'=>array(),
                                           'successors'=>array() );

                }
            }

            fclose($handle);
        }

        function getSuccessors(array $node) {

            $handle = fopen("nodes/adj-1f.txt", "r");

            if ($handle) {
                $lengths = array();

                while (($line = fgets($handle)) !== false) {
                    $line = trim($line);

                    $lineID = substr($line, 1, strpos($line, ':') - 1);

                    if ($node['id'] == intval($lineID)) {
                        $line = substr($line, strpos($line, ':') + 1, strlen($line));

                        $length = 0;
                        for ($i = 0; $i < strlen($line); $i++) {
                            $length++;
                            if ($line[$i] == '-') {
                                $lengths[] = $i;
                                $length = 0;
                            }
                        }

                        $k = 0;

                        for ($i = 0; $i < sizeof($lengths); $i++) {
                            $node['successors'][] =& $this->nodes[intval(substr($line, $k, $lengths[$i])) -1];
                            $k = $lengths[$i] + 1;
                        }
                    }
                }
            }

            fclose($handle);

            return $node;
        }

    }

    function addToList($node, array $list) {
        $list[] =& $node;
        return $list;
    }

    function removeFromList($node, array $list) {
        $index = 0;

        foreach ($list as $item) {
            if ($node['id'] == $item['id']) {
                array_splice($list, $index, 1);
                break;
            }

            $index++;
        }

        return $list;
    }

    function isInList($node, array $list) {
        foreach ($list as $listNode) {
            if ($node['id'] == $listNode['id']) {
                return true;
            }
        }

        return false;
    }

    function f() {
        echo "\n";
        for ($i = 0; $i < 90; $i++) {
            echo "=";
        }
    }

    function d($m, $v) {
        echo $m . ': ';
        var_dump($v);
        echo "\n";
    }

    function p($m) {
        echo $m . "\n";
    }

    function outputList(array $list, $label) {
        echo $label . " List IDs: ";
        foreach ($list as $node) {
            echo $node['id'] . " ";
        }
        echo "\n";
    }
